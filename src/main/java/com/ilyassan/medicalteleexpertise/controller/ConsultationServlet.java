package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Priority;
import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.enums.Status;
import com.ilyassan.medicalteleexpertise.model.Consultation;
import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.Queue;
import com.ilyassan.medicalteleexpertise.model.TechnicalAct;
import com.ilyassan.medicalteleexpertise.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/consultation/*")
public class ConsultationServlet extends BaseServlet {

    public static double CONSULTATION_PRICE = 150.0;

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = User.find(userId);
        if (user == null || user.getRole() != Role.GENERALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        List<Consultation> consultations = Consultation.all();

        request.setAttribute("user", user);
        request.setAttribute("consultations", consultations);
        view(request, response, "consultation_list.jsp");
    }

    public void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = User.find(userId);
        if (user == null || user.getRole() != Role.GENERALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String queueIdParam = request.getParameter("queueId");
        if (queueIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/queue");
            return;
        }

        try {
            Long queueId = Long.parseLong(queueIdParam);
            Queue queue = Queue.find(queueId);
            if (queue == null) {
                request.setAttribute("error", "Queue entry not found.");
                response.sendRedirect(request.getContextPath() + "/queue");
                return;
            }

            Patient patient = queue.getPatient();
            List<TechnicalAct> technicalActs = TechnicalAct.all();

            // Get all specialists
            List<User> specialists = getAllSpecialists();

            // Get unavailable slots for each specialist (for today only)
            Map<Long, List<String>> unavailableSlots = getUnavailableSlotsForToday(specialists);

            request.setAttribute("user", user);
            request.setAttribute("queue", queue);
            request.setAttribute("patient", patient);
            request.setAttribute("technicalActs", technicalActs);
            request.setAttribute("specialists", specialists);
            request.setAttribute("unavailableSlots", unavailableSlots);
            view(request, response, "consultation_form.jsp");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/queue");
        }
    }

    public void store(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = User.find(userId);
        if (user == null || user.getRole() != Role.GENERALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            Long queueId = Long.parseLong(request.getParameter("queueId"));
            String needSpecialist = request.getParameter("needSpecialist");
            String observations = request.getParameter("observations");
            String opinion = request.getParameter("opinion");
            String recommendations = request.getParameter("recommendations");
            String priorityStr = request.getParameter("priority");
            String[] technicalActIds = request.getParameterValues("technicalActIds");
            String specialistIdStr = request.getParameter("specialistId");
            String selectedDateTime = request.getParameter("selectedDateTime");

            Queue queue = Queue.find(queueId);
            if (queue == null) {
                request.setAttribute("error", "Queue entry not found.");
                response.sendRedirect(request.getContextPath() + "/queue");
                return;
            }

            Consultation consultation = new Consultation();
            consultation.setPatient(queue.getPatient());
            consultation.setGeneralist(user);
            consultation.setObservations(observations);

            // Set priority
            Priority priority = Priority.valueOf(priorityStr);
            consultation.setPriority(priority);

            if ("yes".equals(needSpecialist)) {
                // Scenario B: Request specialist opinion
                consultation.setStatus(Status.PENDING_SPECIALIST_OPINION);

                // Validate that specialist and time are selected
                if (specialistIdStr == null || specialistIdStr.isEmpty()) {
                    throw new IllegalArgumentException("Specialist must be selected when requesting specialist opinion");
                }
                if (selectedDateTime == null || selectedDateTime.trim().isEmpty() || selectedDateTime.contains("--")) {
                    throw new IllegalArgumentException("Time slot must be selected when requesting specialist opinion");
                }

                Long specialistId = Long.parseLong(specialistIdStr);
                User specialist = User.find(specialistId);
                if (specialist == null) {
                    throw new IllegalArgumentException("Selected specialist not found");
                }
                consultation.setSpecialist(specialist);

                // Parse and set the selected date time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime appointmentTime = LocalDateTime.parse(selectedDateTime, formatter);
                consultation.setDate(appointmentTime);
            } else {
                // Scenario A: Generalist completes the consultation
                consultation.setOpinion(opinion);
                consultation.setRecommendations(recommendations);
                consultation.setStatus(Status.COMPLETED);
            }

            double totalCost = CONSULTATION_PRICE;

            if (technicalActIds != null && technicalActIds.length > 0) {
                List<TechnicalAct> selectedActs = new ArrayList<>();
                for (String actId : technicalActIds) {
                    TechnicalAct act = TechnicalAct.find(Long.parseLong(actId));
                    if (act != null) {
                        selectedActs.add(act);
                        totalCost += act.getPrice();
                    }
                }
                consultation.setTechnicalActs(selectedActs);
            }

            // Add specialist tariff
            if (consultation.getSpecialist() != null && consultation.getSpecialist().getTariff() != null) {
                totalCost += consultation.getSpecialist().getTariff();
            }

            consultation.setCost(totalCost);
            consultation.create();

            // Remove patient from queue
            queue.delete();

            response.sendRedirect(request.getContextPath() + "/consultation");
        } catch (Exception e) {
            HttpSession errorSession = request.getSession();
            errorSession.setAttribute("error", "Error creating consultation: " + e.getMessage() + " - " + e.getClass().getSimpleName());
            response.sendRedirect(request.getContextPath() + "/queue");
        }
    }

    /**
     * Get all users with SPECIALIST role
     */
    private List<User> getAllSpecialists() {
        return User.all().stream()
                .filter(u -> u.getRole() == Role.SPECIALIST)
                .collect(Collectors.toList());
    }

    /**
     * Get unavailable time slots for each specialist for today only
     * Returns a map of specialist ID to list of time strings (HH:mm format)
     */
    private Map<Long, List<String>> getUnavailableSlotsForToday(List<User> specialists) {
        Map<Long, List<String>> unavailableSlots = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        for (User specialist : specialists) {
            // Get all consultations for this specialist for today
            List<String> bookedSlots = Consultation.all().stream()
                    .filter(c -> c.getSpecialist() != null)
                    .filter(c -> c.getSpecialist().getId().equals(specialist.getId()))
                    .filter(c -> c.getDate() != null)
                    .filter(c -> !c.getDate().isBefore(startOfDay) && !c.getDate().isAfter(endOfDay))
                    .map(c -> {
                        // Extract time in HH:mm format
                        LocalTime time = c.getDate().toLocalTime();
                        return String.format("%02d:%02d", time.getHour(), time.getMinute());
                    })
                    .collect(Collectors.toList());

            unavailableSlots.put(specialist.getId(), bookedSlots);
        }

        return unavailableSlots;
    }
}
