package com.mba.saasapp.security;



import com.mba.saasapp.config.TenantContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Ignorer les routes publiques d'authentification
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/v1/auth")) {
            return true;
        }

        // 2. Extraire le Tenant ID depuis les en-têtes de la requête
        final String tenantId = request.getHeader("X-Tenant-ID");

        // 3. Valider la présence du Tenant ID
        if (tenantId == null || tenantId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing X-Tenant-ID header");
            return false;
        }

        // 4. Stocker le tenant dans le contexte du thread courant
        TenantContext.setCurrentTenant(tenantId);
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Rien à faire ici
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // C'est ICI et SEULEMENT ICI qu'on nettoie le thread, une fois la réponse totalement envoyée au client !
        TenantContext.clear();
    }




}

