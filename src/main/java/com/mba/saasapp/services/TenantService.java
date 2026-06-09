package com.mba.saasapp.services;


import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.entities.requests.RegisterTenantRequest;
import com.mba.saasapp.entities.responses.TenantResponse;

public interface TenantService        {



    void registerTenant(final RegisterTenantRequest request);

    void approveTenant(final String tenantId);

    void activateTenant(final String tenantId);

    void deactivateTenant(final String tenantId);

    void suspendTenant(final String tenantId);

    PageResponse<TenantResponse> findAll(final int page, final int size);
}
