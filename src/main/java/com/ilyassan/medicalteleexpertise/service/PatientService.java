package com.ilyassan.medicalteleexpertise.service;

import com.ilyassan.medicalteleexpertise.model.Patient;

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
}
