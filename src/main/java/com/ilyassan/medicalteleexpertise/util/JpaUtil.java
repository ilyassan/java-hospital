package com.ilyassan.medicalteleexpertise.util;

import jakarta.persistence.EntityManagerFactory;

public class JpaUtil {

    private static EntityManagerFactory entityManagerFactory;

    public static void setEntityManagerFactory(EntityManagerFactory emf) {
        entityManagerFactory = emf;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static void shutdown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
