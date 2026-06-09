package com.mba.saasapp.security;




import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component("applicationAuditorProvider")
public class ApplicationAuditAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si l'utilisateur n'est pas connecté (ex: création du 1er admin)
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM_REGISTRATION");
        }

        // Si l'utilisateur est connecté, on retourne son username dynamiquement
        return Optional.of(authentication.getName());
    }
}
