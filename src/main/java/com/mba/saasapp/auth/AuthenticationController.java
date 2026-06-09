package com.mba.saasapp.auth;

import com.mba.saasapp.auth.Request.LoginRequest;
import com.mba.saasapp.auth.Response.LoginResponse;
import com.mba.saasapp.auth.service.AuthenticationService;
import com.mba.saasapp.entities.requests.RegisterTenantRequest;
import com.mba.saasapp.entities.responses.TenantResponse;
import com.mba.saasapp.services.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping( "api/v1/auth" )
@RestController
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TenantService tenantService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody final LoginRequest request
    ) {
        final LoginResponse response = this.authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<TenantResponse> register(
            @Valid @RequestBody final RegisterTenantRequest request
    ) {
        this.tenantService.registerTenant(request);

        TenantResponse response = TenantResponse.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .adminUsername(request.getAdminUsername())
                // .message("Tenant créé avec succès !")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}

