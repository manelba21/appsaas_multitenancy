package com.mba.saasapp.repositories;

import com.mba.saasapp.entities.Tenant;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant ,String> {


    boolean existsByCompanyCode(String companyCode);

    boolean existsByEmail(String email);
}
