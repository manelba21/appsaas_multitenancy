package com.mba.saasapp.entities.responses;

import com.mba.saasapp.entities.TenantStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantResponse {


    private String tenantId;
    private String companyName;
    private String companyCode;
    private String email;
    private String adminFullName;
    private String adminEmail;
    private String adminUsername;
    private LocalDateTime createdAt ;
    private String adminPassword;

    private TenantStatus status;
}
