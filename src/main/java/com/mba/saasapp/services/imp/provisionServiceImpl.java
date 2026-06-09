package com.mba.saasapp.services.imp;

import com.mba.saasapp.config.TenantContext;
import com.mba.saasapp.entities.Tenant;
import com.mba.saasapp.entities.User;
import com.mba.saasapp.entities.UserRole;
import com.mba.saasapp.exceptions.TenantProvisioningException;
import com.mba.saasapp.repositories.UserRepository;
import com.mba.saasapp.services.ProvisionningService;
import com.mba.saasapp.services.TenantMigrationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.transaction.annotation.Propagation;

@Service
@RequiredArgsConstructor
@Slf4j


public class provisionServiceImpl   implements ProvisionningService {
    private  final JdbcTemplate  jdbcTemplate ;
    private final DataSource  dataSource ;
    private final UserRepository userRepository ;
    private final TenantMigrationService tenantMigrationService;

    private provisionServiceImpl self;
    @Autowired
    private PasswordEncoder passwordEncoder; // <-- AJOUTEZ CETTE LIGNE

    @Autowired
    public void setSelf(@Lazy provisionServiceImpl self) {
        this.self = self;
    }
    @Override
    public void provisionTenant(final Tenant tenant) {

        final String schemaName = "tenant_" + tenant.getCompanyName()
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");

        try {
            log.info("Provisioning tenant: {} (schema: {})", tenant.getCompanyName(), schemaName);
            // 1. Create the Postgres schema
            createSchema(schemaName);
            log.info("Schema created successfully: {}", schemaName);

            // 2. Run Flyway migrations for this schema
           ;
            // Dans provisionServiceImpl.java, au lieu d'appeler directement :
// tenantMigrationService.runTenantMigrations(schemaName);

// Utilisez un thread indépendant pour casser le blocage :
            new Thread(() -> {
                try {
                    tenantMigrationService.runTenantMigrations(schemaName);
                } catch (Exception e) {
                    log.error("Erreur asynchrone Flyway", e);
                }
            }).start();


            log.info("Tenant migrations completed successfully for schema: {}", schemaName);

            // 3. Initialize the default data (optional)
            initializeDefaultData(schemaName, tenant);
        } catch (final Exception e) {
            // 1. Log TOUTE la stacktrace de l'erreur d'origine (très important !)
            log.error("--- ERREUR CRITIQUE DURANT LE PROVISIONING ---");
            log.error("Message d'erreur : {}", e.getMessage(), e);

            // 2. Commentez temporairement le rollback pour éviter les effets de bord
    /*
    try {
        dropSchema(schemaName);
    } catch (final Exception exp) {
        log.error("Failed to rollback schema creation", exp);
    }
    */

            // 3. Renvoyez l'erreur d'origine au lieu d'une exception générique Custom
            throw new RuntimeException("Erreur d'origine : " + e.getMessage(), e);
        }

    }
    @Transactional
    private void initializeDefaultData(final String schemaName, final Tenant tenant) {
        log.info("Initializing default data for tenant: {}", tenant.getCompanyName());

        // 1. Changement dynamique du contexte vers le bon schéma
        TenantContext.setCurrentTenant(schemaName);

        try {
            User adminUser = new User();
            adminUser.setUsername(tenant.getAdminUsername());
            adminUser.setEmail(tenant.getAdminEmail());
            adminUser.setPassword(passwordEncoder.encode(tenant.getAdminPassword()));
            adminUser.setCreatedBy("SYSTEM");
            adminUser.setCreatedAt(LocalDateTime.now());

            // --- Découpage dynamique du Nom Complet ---
            String fullName = tenant.getAdminFullName() != null ? tenant.getAdminFullName().trim() : "";
            String firstName = "Admin"; // Valeurs par défaut au cas où
            String lastName = "Tenant";

            if (!fullName.isEmpty()) {
                String[] nameParts = fullName.split("\\s+", 2); // Découpe au premier espace trouvé
                firstName = nameParts[0];
                if (nameParts.length > 1) {
                    lastName = nameParts[1];
                } else {
                    lastName = nameParts[0]; // Si un seul mot est fourni
                }
            }

            adminUser.setFirstName(firstName);
            adminUser.setLastName(lastName);
            // ------------------------------------------

            // Sauvegarde de l'objet complet
            this.userRepository.save(adminUser);
            log.info("Admin user '{}' created successfully for schema: {}", tenant.getAdminUsername(), schemaName);



            // 3. Sauvegarde dans la table du nouveau schéma
        //    this.userRepository.save(admin);
          //  log.info("Admin user '{}' created successfully for schema: {}", tenant.getAdminUsername(), schemaName);

        } finally {
            // 4. Nettoyage sécurisé du contexte
            TenantContext.clear();
        }
    }



    private void createSchema(final String schemaName) {
        String sql = "CREATE SCHEMA IF NOT EXISTS \"" + schemaName + "\"";

        this.jdbcTemplate.execute(sql);
    }
    private void dropSchema(final String schemaName) {
        final String sql = String.format("DROP SCHEMA IF EXISTS %s CASCADE", schemaName);
        this.jdbcTemplate.execute(sql);
    }

}
