package com.mba.saasapp;

import com.mba.saasapp.Properties.JwtProperties;
import com.mba.saasapp.security.ApplicationAuditAware;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "applicationAuditorProvider") // 👈 Changez le nom ici

@EnableConfigurationProperties(JwtProperties.class)
//@EntityScan(basePackages = "com.mba.saasapp.entities")
public class SaasAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasAppApplication.class, args);
    }

}
