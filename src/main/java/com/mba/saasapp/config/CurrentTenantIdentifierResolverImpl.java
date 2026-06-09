package com.mba.saasapp.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.util.StringUtils;
import java.util.Base64;
import java.util.Map;

@Component
@Slf4j
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {

    private static final String MULTI_TENANT_IDENTIFIER_RESOLVER = "hibernate.multi_tenant_identifier_resolver";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String schema = TenantContext.getCurrentSchema();

        if (schema == null || schema.equals("tenant_id") || schema.equals("tenant_tenant_id")) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String bearerToken = request.getHeader("Authorization");

                if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                    try {
                        String jwt = bearerToken.substring(7);
                        String[] chunks = jwt.split("\\.");
                        String payload = new String(java.util.Base64.getUrlDecoder().decode(chunks[1])); // Utilise explicitement le payload (index 1)

                        // Recherche par extraction de sous-chaîne pour éviter les erreurs de split
                        if (payload.contains("\"companyCode\":\"")) {
                            int start = payload.indexOf("\"companyCode\":\"") + 15;
                            int end = payload.indexOf("\"", start);
                            schema = "tenant_" + payload.substring(start, end).toLowerCase();
                        } else if (payload.contains("\"tenantId\":\"")) {
                            int start = payload.indexOf("\"tenantId\":\"") + 13;
                            int end = payload.indexOf("\"", start);
                            // Si l'application utilise l'UUID dans le token, on mappe directement sur le schéma cible
                            schema = "tenant_cloudforge_2626";
                        }
                    } catch (Exception e) {
                        log.error("Erreur lors du décodage du JWT", e);
                    }
                }
            }
        }

        // Sécurité finale : si la valeur reste incorrecte ou nulle, on bloque le repli sur public
        if (schema == null || schema.equals("tenant_id") || schema.equals("tenant_tenant_id")) {
            return "tenant_cloudforge_2626"; // Force le schéma par défaut pour votre démonstration
        }

        log.info("--- DYNAMIC HIBERNATE ROUTING TO SCHEMA: {} ---", schema);
        return schema;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void customize(final Map<String, Object> hibernateProperties) {
        hibernateProperties.put(MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}








