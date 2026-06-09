package com.mba.saasapp.config;

public class TenantContext {

        private static final ThreadLocal<String> CURRENT_TENANT =new InheritableThreadLocal<>();;
    private static final ThreadLocal<String> CURRENT_SCHEMA = new InheritableThreadLocal<>();;
        /**
         * Définit l'identifiant du tenant pour le thread courant.
         */
        public static void setCurrentTenant(final String tenant) {
            CURRENT_TENANT.set(tenant);
        }
        public static void setCurrentSchema(final String schema) {
        CURRENT_SCHEMA.set(schema);
    }
        /**
         * Récupère l'identifiant du tenant pour le thread courant.
         */
        public static String getCurrentTenant() {
            return CURRENT_TENANT.get();
        }
         public static String getCurrentSchema() {
        return CURRENT_SCHEMA.get();
    }

        /**
         * Nettoie le tenant du thread courant.
         * IMPORTANT : doit être appelé dans un bloc finally
         * pour éviter les fuites de mémoire (memory leak).
         */
        public static void clear() {

            CURRENT_TENANT.remove();
            CURRENT_SCHEMA.remove();
        }
    }



