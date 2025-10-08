package com.ilyassan.medicalteleexpertise.service;

import com.ilyassan.medicalteleexpertise.enums.Priority;
import com.ilyassan.medicalteleexpertise.enums.Status;
import com.ilyassan.medicalteleexpertise.model.Consultation;
import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.TechnicalAct;
import com.ilyassan.medicalteleexpertise.model.User;
import com.ilyassan.medicalteleexpertise.repository.ConsultationRepository;

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

    private final ConsultationRepository consultationRepository = new ConsultationRepository();
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

    public Long getConsultationsCountOfSpecialist(Long id){
        return Consultation.all().stream()
                    .filter(c -> {
                        return c.getSpecialist() != null && c.getSpecialist().getId().equals(id);
                    })
                    .count();
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

    public Map<String, Consultation> getSpecialistTodayAgenda(Long specialistId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Get all consultations for this specialist today
        List<Consultation> consultations = Consultation.all().stream()
                .filter(c -> c.getSpecialist() != null)
                .filter(c -> c.getSpecialist().getId().equals(specialistId))
                .filter(c -> c.getDate() != null)
                .filter(c -> !c.getDate().isBefore(startOfDay) && !c.getDate().isAfter(endOfDay))
                .collect(Collectors.toList());

        // Map time slots to consultations
        Map<String, Consultation> timeSlotConsultations = new HashMap<>();
        for (Consultation consultation : consultations) {
            LocalTime time = consultation.getDate().toLocalTime();
            String timeSlot = String.format("%02d:%02d", time.getHour(), time.getMinute());
            timeSlotConsultations.put(timeSlot, consultation);
        }

        return timeSlotConsultations;
    }

    public List<String> generateAllTimeSlots() {
        List<String> timeSlots = new ArrayList<>();

        // Morning slots: 8:00 AM - 12:00 PM (8 slots of 30 minutes)
        for (int hour = 8; hour < 12; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                timeSlots.add(String.format("%02d:%02d", hour, minute));
            }
        }

        // Afternoon slots: 2:00 PM - 6:00 PM (8 slots of 30 minutes)
        for (int hour = 14; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                timeSlots.add(String.format("%02d:%02d", hour, minute));
            }
        }

        return timeSlots;
    }

    public Consultation findById(Long id) {
        return consultationRepository.findByIdWithTechnicalActs(id);
    }

    public void completeSpecialistConsultation(Long consultationId, String opinion, String recommendations) {
        Consultation consultation = Consultation.find(consultationId);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation not found");
        }

        consultation.setOpinion(opinion);
        consultation.setRecommendations(recommendations);
        consultation.setStatus(Status.COMPLETED);
        consultation.update();
    }
}
