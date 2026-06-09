package com.mba.saasapp.Controllers;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.entities.responses.TenantResponse;
import com.mba.saasapp.services.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/tenants")
@Tag(name = "Tenant", description = "Tenant API")
public class TenantController {


    private final TenantService service ;

    @PostMapping("/approve/{tenant-id}")
    public ResponseEntity<Void> approveTenant(
            @PathVariable("tenant-id")
            final String tenantId
    ) {
        this.service.approveTenant(tenantId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate/{tenant-id}")
    public ResponseEntity<Void> activateTenant(
            @PathVariable("tenant-id")
            final String tenantId
    ) {
        this.service.activateTenant(tenantId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deactivate/{tenant-id}")
    public ResponseEntity<Void> deactivateTenant(
            @PathVariable("tenant-id")
            final String tenantId
    ) {
        this.service.deactivateTenant(tenantId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/suspend/{tenant-id}")
    public ResponseEntity<Void> suspendTenant(
            @PathVariable("tenant-id")
            final String tenantId
    ) {
        this.service.suspendTenant(tenantId);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<PageResponse<TenantResponse>> findAllTenants(
            @RequestParam(name = "page", defaultValue = "0")
            final int page,
            @RequestParam(name = "size", defaultValue = "10")
            final int size
    ) {
        return ResponseEntity.ok(this.service.findAll(page, size));
    }
}
