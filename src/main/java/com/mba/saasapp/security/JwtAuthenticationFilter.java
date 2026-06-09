package com.mba.saasapp.security;

import com.mba.saasapp.config.TenantContext;


import com.mba.saasapp.config.TenantSchemaResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter   extends OncePerRequestFilter {


    private  final  JwtTokenService jwtTokenService;
    private  final TenantSchemaResolver  tenantSchemaResolver ;
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        //  extraction du token, validation et récupération des variables userId, role, tenantId
        if (request.getRequestURI().startsWith("/api/v1/auth/"))  {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                try {
                    if (this.jwtTokenService.validateToken(jwt)) {
                        final String userId = this.jwtTokenService.getUserIdFromToken(jwt);
                        final String tenantId = this.jwtTokenService.getTenantIdFromToken(jwt);
                        final String role = this.jwtTokenService.getRoleFromToken(jwt);

                        if (tenantId != null) {

                            // Stocker le tenant ID et le schemaName
                            TenantContext.setCurrentTenant(tenantId);
                            final String schemaName = this.tenantSchemaResolver.resolveTenantSchema(tenantId);
                            TenantContext.setCurrentSchema(schemaName);
                            // Stocker le tenant ID si nécessaire pour votre logique multi-tenant
                        }

                        // ⚠️ LA PIÈCE MANQUANTE : Enregistrer l'utilisateur et son rôle dans Spring Security
                        if (userId != null && role != null) {
                            java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities =
                                    java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));

                            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication =
                                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userId, null, authorities);

                            authentication.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));
                            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                } catch (io.jsonwebtoken.ExpiredJwtException e) {
                    log.warn("Le token a expiré, mais il est toléré pour le développement.");

                    // Optionnel : Vous pouvez forcer l'authentification d'un admin même si le token a expiré pour vos tests
                    final String userId = "admin_test";
                    java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities =
                            java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("PLATFORM_ADMIN"));
                    org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication =
                            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userId, null, authorities);
                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (final Exception e) {
            log.error("Error authenticating user", e);
        }

        // TRÈS IMPORTANT : Cette ligne doit TOUJOURS être exécutée à la fin de la méthode
        filterChain.doFilter(request, response);
    }


    private String getJwtFromRequest(final HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
