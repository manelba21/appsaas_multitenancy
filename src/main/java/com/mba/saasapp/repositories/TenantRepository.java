package com.mba.saasapp.repositories;

import com.mba.saasapp.entities.Tenant;
import com.mba.saasapp.entities.TenantStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TenantRepository extends JpaRepository<Tenant ,String> {


    boolean existsByCompanyCode(String companyCode);

    boolean existsByEmail(String email);


// ... dans votre interface TenantRepository :

    @Transactional
    @Modifying
    @Query("UPDATE Tenant t SET t.status = :status WHERE t.id = :id")
    void updateStatus(@Param("id") String id, @Param("status") TenantStatus status);


}
