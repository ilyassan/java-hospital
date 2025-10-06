package com.ilyassan.medicalteleexpertise.seeder;

import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.enums.Specialty;
import com.ilyassan.medicalteleexpertise.model.User;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.List;

@WebListener
public class UserSeeder implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[UserSeeder] Starting user seeding...");

        try {
            // Check if users already exist
            List<User> existingUsers = User.all();
            if (!existingUsers.isEmpty()) {
                System.out.println("[UserSeeder] Users already exist. Skipping seeding.");
                return;
            }

            // Seed 2 Nurses
            createNurse("Sarah", "Johnson", "sarah.johnson@hospital.com", "555-0101");
            createNurse("Michael", "Davis", "michael.davis@hospital.com", "555-0102");

            // Seed 3 Generalists
            createGeneralist("Emily", "Brown", "emily.brown@hospital.com", "555-0201");
            createGeneralist("David", "Wilson", "david.wilson@hospital.com", "555-0202");
            createGeneralist("Lisa", "Martinez", "lisa.martinez@hospital.com", "555-0203");

            // Seed Specialists (one for each specialty)
            createSpecialist("James", "Anderson", "james.anderson@hospital.com", Specialty.CARDIOLOGY, 150.0);
            createSpecialist("Patricia", "Taylor", "patricia.taylor@hospital.com", Specialty.PNEUMOLOGY, 140.0);
            createSpecialist("Robert", "Thomas", "robert.thomas@hospital.com", Specialty.NEUROLOGY, 160.0);
            createSpecialist("Jennifer", "Moore", "jennifer.moore@hospital.com", Specialty.GASTROENTEROLOGY, 145.0);
            createSpecialist("William", "Jackson", "william.jackson@hospital.com", Specialty.ENDOCRINOLOGY, 155.0);
            createSpecialist("Linda", "White", "linda.white@hospital.com", Specialty.DERMATOLOGY, 135.0);
            createSpecialist("Richard", "Harris", "richard.harris@hospital.com", Specialty.RHEUMATOLOGY, 150.0);
            createSpecialist("Barbara", "Martin", "barbara.martin@hospital.com", Specialty.PSYCHIATRY, 165.0);
            createSpecialist("Joseph", "Garcia", "joseph.garcia@hospital.com", Specialty.NEPHROLOGY, 155.0);
            createSpecialist("Susan", "Rodriguez", "susan.rodriguez@hospital.com", Specialty.ORTHOPEDICS, 170.0);

            System.out.println("[UserSeeder] User seeding completed successfully.");
        } catch (Exception e) {
            System.err.println("[UserSeeder] Error during seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createNurse(String firstName, String lastName, String email, String phone) {
        User nurse = new User();
        nurse.setFirstName(firstName);
        nurse.setLastName(lastName);
        nurse.setEmail(email);
        nurse.setPassword("password123");
        nurse.setRole(Role.NURSE);
        nurse.setPhone(phone);
        nurse.create();
        System.out.println("[UserSeeder] Created nurse: " + firstName + " " + lastName);
    }

    private void createGeneralist(String firstName, String lastName, String email, String phone) {
        User generalist = new User();
        generalist.setFirstName(firstName);
        generalist.setLastName(lastName);
        generalist.setEmail(email);
        generalist.setPassword("password123");
        generalist.setRole(Role.GENERALIST);
        generalist.setPhone(phone);
        generalist.create();
        System.out.println("[UserSeeder] Created generalist: " + firstName + " " + lastName);
    }

    private void createSpecialist(String firstName, String lastName, String email, Specialty specialty, Double tariff) {
        User specialist = new User();
        specialist.setFirstName(firstName);
        specialist.setLastName(lastName);
        specialist.setEmail(email);
        specialist.setPassword("password123");
        specialist.setRole(Role.SPECIALIST);
        specialist.setSpecialty(specialty);
        specialist.setTariff(tariff);
        specialist.create();
        System.out.println("[UserSeeder] Created specialist (" + specialty + "): " + firstName + " " + lastName);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do on shutdown
    }
}
