package com.mba.saasapp.config;




import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantHibernateFilter {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.mba.saasapp.services.*.*(..))")
    public void activateTenantFilter() {
        final String tenantId = TenantContext.getCurrentTenant();

        if (tenantId != null) {
            final Session session = this.entityManager.unwrap(Session.class);

            // active le filtre et injecte le paramètre tenantId
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId);
        }
    }
}



