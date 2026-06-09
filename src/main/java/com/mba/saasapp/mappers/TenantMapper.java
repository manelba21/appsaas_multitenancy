package com.mba.saasapp.mappers;

import com.mba.saasapp.entities.Tenant;
import com.mba.saasapp.entities.requests.RegisterTenantRequest;
import com.mba.saasapp.entities.responses.TenantResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class TenantMapper {

    public Tenant toEntity(final RegisterTenantRequest request) {
        return Tenant.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .createdAt(LocalDateTime.now())
                .email(request.getEmail())
                .adminFullName(request.getAdminFullName())
                .adminEmail(request.getAdminEmail())
                .adminUsername(request.getAdminUsername())
                .build();
    }

    public TenantResponse toResponse(final Tenant tenant) {
        return TenantResponse.builder()
                .companyName(tenant.getCompanyName())
                .companyCode(tenant.getCompanyCode())
                .createdAt(tenant.getCreatedAt())
                .email(tenant.getEmail())
                .adminFullName(tenant.getAdminFullName())
                .adminEmail(tenant.getAdminEmail())
                .adminUsername(tenant.getAdminUsername())
                .build();
    }

}
