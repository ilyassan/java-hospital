package com.ilyassan.medicalteleexpertise.service;

import com.ilyassan.medicalteleexpertise.enums.Priority;
import com.ilyassan.medicalteleexpertise.enums.Status;
import com.ilyassan.medicalteleexpertise.model.Consultation;
import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.TechnicalAct;
import com.ilyassan.medicalteleexpertise.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConsultationService {

    public static final double CONSULTATION_PRICE = 150.0;

    public List<Consultation> getAllConsultations() {
        return Consultation.all();
    }

    public Map<Long, List<String>> getUnavailableSlotsForToday(List<User> specialists) {
        Map<Long, List<String>> unavailableSlots = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        for (User specialist : specialists) {
            List<String> bookedSlots = Consultation.all().stream()
                    .filter(c -> c.getSpecialist() != null)
                    .filter(c -> c.getSpecialist().getId().equals(specialist.getId()))
                    .filter(c -> c.getDate() != null)
                    .filter(c -> !c.getDate().isBefore(startOfDay) && !c.getDate().isAfter(endOfDay))
                    .map(c -> {
                        LocalTime time = c.getDate().toLocalTime();
                        return String.format("%02d:%02d", time.getHour(), time.getMinute());
                    })
                    .collect(Collectors.toList());

            unavailableSlots.put(specialist.getId(), bookedSlots);
        }

        return unavailableSlots;
    }

    public Consultation createConsultationWithSpecialist(
            Patient patient,
            User generalist,
            String observations,
            Priority priority,
            User specialist,
            LocalDateTime appointmentTime,
            List<TechnicalAct> technicalActs
    ) {
        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setGeneralist(generalist);
        consultation.setObservations(observations);
        consultation.setPriority(priority);
        consultation.setStatus(Status.PENDING_SPECIALIST_OPINION);
        consultation.setSpecialist(specialist);
        consultation.setDate(appointmentTime);
        consultation.setTechnicalActs(technicalActs);

        double totalCost = calculateConsultationCost(technicalActs, specialist);
        consultation.setCost(totalCost);

        consultation.create();
        return consultation;
    }

    public Consultation createConsultationWithoutSpecialist(
            Patient patient,
            User generalist,
            String observations,
            String opinion,
            String recommendations,
            Priority priority,
            List<TechnicalAct> technicalActs
    ) {
        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setGeneralist(generalist);
        consultation.setObservations(observations);
        consultation.setOpinion(opinion);
        consultation.setRecommendations(recommendations);
        consultation.setPriority(priority);
        consultation.setStatus(Status.COMPLETED);
        consultation.setTechnicalActs(technicalActs);

        double totalCost = calculateConsultationCost(technicalActs, null);
        consultation.setCost(totalCost);

        consultation.create();
        return consultation;
    }

    public double calculateConsultationCost(List<TechnicalAct> technicalActs, User specialist) {
        double totalCost = CONSULTATION_PRICE;

        if (technicalActs != null && !technicalActs.isEmpty()) {
            totalCost += technicalActs.stream()
                    .mapToDouble(TechnicalAct::getPrice)
                    .sum();
        }

        if (specialist != null && specialist.getTariff() != null) {
            totalCost += specialist.getTariff();
        }

        return totalCost;
    }

    public LocalDateTime parseDateTime(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    public void validateSpecialistSelection(String specialistIdStr, String selectedDateTime) {
        if (specialistIdStr == null || specialistIdStr.isEmpty()) {
            throw new IllegalArgumentException("Specialist must be selected when requesting specialist opinion");
        }
        if (selectedDateTime == null || selectedDateTime.trim().isEmpty() || selectedDateTime.contains("--")) {
            throw new IllegalArgumentException("Time slot must be selected when requesting specialist opinion");
        }
    }

    public List<TechnicalAct> getTechnicalActsByIds(String[] actIds) {
        List<TechnicalAct> acts = new ArrayList<>();
        if (actIds != null && actIds.length > 0) {
            for (String actId : actIds) {
                TechnicalAct act = TechnicalAct.find(Long.parseLong(actId));
                if (act != null) {
                    acts.add(act);
                }
            }
        }
        return acts;
    }
}
