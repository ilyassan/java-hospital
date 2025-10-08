package com.ilyassan.medicalteleexpertise.seeder;

import com.ilyassan.medicalteleexpertise.model.TechnicalAct;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.List;

@WebListener
public class TechnicalActSeeder implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[TechnicalActSeeder] Starting technical act seeding...");

        try {
            // Check if technical acts already exist
            List<TechnicalAct> existingActs = TechnicalAct.all();
            if (!existingActs.isEmpty()) {
                System.out.println("[TechnicalActSeeder] Technical acts already exist. Skipping seeding.");
                return;
            }

            // Seed technical acts
            createTechnicalAct("RAD-001", "Chest X-Ray", "Radiographic examination of the chest", 80.0);
            createTechnicalAct("RAD-002", "Abdominal Scan", "CT scan of the abdomen", 350.0);
            createTechnicalAct("LAB-001", "Complete Blood Count", "Full blood analysis including RBC, WBC, platelets", 50.0);
            createTechnicalAct("LAB-002", "Urine Analysis", "Complete urinalysis for metabolic and kidney function", 40.0);
            createTechnicalAct("CARD-001", "Electrocardiogram (ECG)", "Recording of the heart's electrical activity", 60.0);
            createTechnicalAct("IMAG-001", "MRI Scan", "Magnetic resonance imaging scan", 800.0);
            createTechnicalAct("IMAG-002", "Ultrasound", "Ultrasound imaging examination", 120.0);
            createTechnicalAct("ENDO-001", "Endoscopy", "Internal examination using endoscope", 450.0);

            System.out.println("[TechnicalActSeeder] Technical act seeding completed successfully.");
        } catch (Exception e) {
            System.err.println("[TechnicalActSeeder] Error during seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTechnicalAct(String code, String name, String description, Double price) {
        TechnicalAct act = new TechnicalAct();
        act.setCode(code);
        act.setName(name);
        act.setDescription(description);
        act.setPrice(price);
        act.create();
        System.out.println("[TechnicalActSeeder] Created technical act: " + code + " - " + name);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do on shutdown
    }
}
