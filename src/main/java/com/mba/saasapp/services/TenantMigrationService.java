package com.mba.saasapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantMigrationService {

    private final DataSource dataSource;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void runTenantMigrations(final String schemaName) {
        log.info("Running tenant migrations for schema: {}", schemaName);

        final Flyway tenantFlyway = Flyway.configure()
                .dataSource(this.dataSource)
                .schemas(schemaName)
                .locations("classpath:db/migration/tenant")
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .load();

        log.info("Tenant migrations started");
        tenantFlyway.migrate();
        log.info("Tenant migrations completed");
    }
}
