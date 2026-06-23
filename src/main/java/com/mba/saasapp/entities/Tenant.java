package com.mba.saasapp.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

import static jakarta.persistence.EnumType.STRING;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@SuperBuilder
@Entity
@Table(name = "tenants")
@EntityListeners(AuditingEntityListener.class)
public class Tenant extends AbstractEntity {


    @Column(name = "company_name", nullable = false)
        private String companyName;

        @Column(name = "company_code", nullable = false, unique = true)
        private String companyCode;

        @Column(name = "email", nullable = false, unique = true)
        private String email;

        @Enumerated(STRING)
        @Column(name = "status", nullable = false)
        private TenantStatus status = TenantStatus.PENDING;

        // initial admin credentials
        @Column(name = "admin_full_name", nullable = false)
        private String adminFullName;

        @Column(name = "admin_email", nullable = false, unique = true)
        private String adminEmail;

        @Column(name = "admin_username", nullable = false, unique = true)
        private String adminUsername;

        @Column(name = "admin_password", nullable = false)
        private String adminPassword;

          @Enumerated(EnumType.STRING)
        @Column(name = "admin_role") // Ce nom doit correspondre à votre colonne en BDD
       private UserRole adminRole;

    @OneToMany(mappedBy = "tenant", cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<User> users;


}


