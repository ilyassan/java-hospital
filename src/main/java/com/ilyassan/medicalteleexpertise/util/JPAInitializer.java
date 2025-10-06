package com.ilyassan.medicalteleexpertise.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class JPAInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[JPAInitializer] Initializing JPA...");
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
            JpaUtil.setEntityManagerFactory(emf);
            System.out.println("[JPAInitializer] JPA Initialized successfully.");
        } catch (Exception e) {
            System.err.println("[JPAInitializer] FATAL ERROR initializing JPA: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize JPA. Application cannot start.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[JPAInitializer] Shutting down JPA...");
        JpaUtil.shutdown();
        System.out.println("[JPAInitializer] JPA shutdown complete.");
    }
}