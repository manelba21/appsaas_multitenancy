package com.mba.saasapp.services.imp;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.entities.Tenant;
import com.mba.saasapp.entities.TenantStatus;
import com.mba.saasapp.entities.User;
import com.mba.saasapp.entities.UserRole;
import com.mba.saasapp.entities.requests.RegisterTenantRequest;
import com.mba.saasapp.entities.responses.TenantResponse;
import com.mba.saasapp.exceptions.DuplicateResourceException;
import com.mba.saasapp.exceptions.InvalidRequestException;
import com.mba.saasapp.mappers.TenantMapper;
import com.mba.saasapp.repositories.TenantRepository;
import com.mba.saasapp.repositories.UserRepository;
import com.mba.saasapp.services.ProvisionningService;
import com.mba.saasapp.services.TenantService;
import io.jsonwebtoken.security.Password;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.mba.saasapp.entities.UserRole.ROLE_COMPANY_ADMIN;

@Service
@RequiredArgsConstructor
@Slf4j


public class TenantServiceImpl   implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder ;
    private final UserRepository userRepository ;
     private final ProvisionningService provisionningService ;
    @Transactional
    public void registerTenant(RegisterTenantRequest request) {
        // 1. Vérifications d'existence
        if (this.tenantRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new DuplicateResourceException("Tenant already exists");
        }

        if (this.tenantRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Tenant Email already exists");
        }

        // 2. Création et configuration initiale de l'entité
        final Tenant tenant = this.tenantMapper.toEntity(request);
        tenant.setAdminPassword(this.passwordEncoder.encode(request.getAdminPassword()));
        tenant.setStatus(TenantStatus.PENDING);

        // 3. UNE SEULE SAUVEGARDE ICI (On supprime le doublon)
        final Tenant savedTenant = this.tenantRepository.save(tenant);

        // 4. Appel du service de provisionnement

        this.provisionningService.provisionTenant(savedTenant);
    }
@Transactional
    public void approveTenant(String tenantId) {
        Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));


        this.tenantRepository.updateStatus(tenantId, TenantStatus.ACTIVE);

        try {

            tenant.setUsers(new java.util.ArrayList<>());


            this.provisionningService.provisionTenant(tenant);

        } catch (Exception e) {
            rollbackTenantStatus(tenant);
            throw e;
        }
    }


    private void rollbackTenantStatus(Tenant tenant) {
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);
    }

    @Override


    public void activateTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new InvalidRequestException("Tenant is not pending");
        }


        tenant.setStatus(TenantStatus.ACTIVE);

        this.tenantRepository.save(tenant);

    }

    @Override
    public void deactivateTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant is not pending");
        }

        tenant.setStatus(TenantStatus.INACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void suspendTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant is not pending");
        }

        tenant.setStatus(TenantStatus.SUSPENDED);
        this.tenantRepository.save(tenant);

    }

    @Override
    public PageResponse<TenantResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Tenant> tenants = this.tenantRepository.findAll(pageRequest);
        final Page<TenantResponse> tenantResponses = tenants.map(this.tenantMapper::toResponse);
        return PageResponse.of(tenantResponses);
    }


    private void createInitialAdminUser(Tenant tenant) {
        User user = new User();
        // Utilise l'email comme identifiant par défaut
        user.setUsername(tenant.getAdminUsername());
        user.setEmail(tenant.getAdminEmail());

        user.setPassword(tenant.getAdminPassword() != null ? tenant.getAdminPassword() : "MotDePasseSecurise123");
        user.setLastName(tenant.getAdminFullName());
        user.setFirstName("Admin");

        // Attribution dynamique du rôle
        if (tenant.getAdminRole() != null) {
            user.setRole(tenant.getAdminRole());
        } else {
            user.setRole(UserRole.ROLE_COMPANY_ADMIN); // <-- Supprimez le ".Role" en trop ici
        }

        user.setEnabled(true);
        if (user.getCreatedBy() == null) {
            user.setCreatedBy("SYSTEM_REGISTRATION");
        }
        this.userRepository.save(user);
    }




    private String extractFirstName(final String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        return fullName.trim().split("\\s+")[0];
    }

    private String extractLastName(final String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        final String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }


}
