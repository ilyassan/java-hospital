package com.ilyassan.medicalteleexpertise.service;

import com.ilyassan.medicalteleexpertise.model.Patient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PatientService {

    public List<Patient> getAllPatients() {
        return Patient.all();
    }

    public Patient findById(Long id) {
        return Patient.find(id);
    }

    public Patient findByCin(String cin) {
        return Patient.all().stream()
                .filter(p -> p.getCin().equals(cin))
                .findFirst()
                .orElse(null);
    }

    public void createPatient(Patient patient) {
        patient.create();
    }

    public void updatePatient(Patient patient) {
        patient.update();
    }

    /**
     * Check if patient's vital signs are outdated (more than 24 hours old)
     * @param patient The patient to check
     * @return true if vital signs are outdated or missing, false otherwise
     */
    public boolean areVitalSignsOutdated(Patient patient) {
        if (patient == null || patient.getVitalSignsTimestamp() == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(patient.getVitalSignsTimestamp(), now);

        // Check if more than 24 hours have passed
        return duration.toHours() >= 24;
    }
}
