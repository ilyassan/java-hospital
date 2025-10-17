package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Priority;
import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.Queue;
import com.ilyassan.medicalteleexpertise.model.TechnicalAct;
import com.ilyassan.medicalteleexpertise.model.User;
import com.ilyassan.medicalteleexpertise.service.ConsultationService;
import com.ilyassan.medicalteleexpertise.service.QueueService;
import com.ilyassan.medicalteleexpertise.service.TechnicalActService;
import com.ilyassan.medicalteleexpertise.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@WebServlet("/consultation/*")
public class ConsultationServlet extends BaseServlet {

    private final ConsultationService consultationService = new ConsultationService();
    private final UserService userService = new UserService();
    private final TechnicalActService technicalActService = new TechnicalActService();
    private final QueueService queueService = new QueueService();

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.GENERALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.setAttribute("user", user);
        request.setAttribute("consultations", consultationService.getAllConsultations());
        view(request, response, "consultation_list.jsp");
    }

    public void show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.GENERALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String consultationIdParam = request.getParameter("id");
        if (consultationIdParam == null || consultationIdParam.isEmpty()) {
            session.setAttribute("error", "Consultation ID is required");
            response.sendRedirect(request.getContextPath() + "/consultation");
            return;
        }

        try {
            Long consultationId = Long.parseLong(consultationIdParam);
            com.ilyassan.medicalteleexpertise.model.Consultation consultation = consultationService.findById(consultationId);

            if (consultation == null) {
                session.setAttribute("error", "Consultation not found");
                response.sendRedirect(request.getContextPath() + "/consultation");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("consultation", consultation);
            view(request, response, "consultation_detail.jsp");
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Invalid consultation ID");
            response.sendRedirect(request.getContextPath() + "/consultation");
        }
    }

    public void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
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
            Queue queue = queueService.findById(queueId);
            if (queue == null) {
                request.setAttribute("error", "Queue entry not found.");
                response.sendRedirect(request.getContextPath() + "/queue");
                return;
            }

            Patient patient = queue.getPatient();
            List<TechnicalAct> technicalActs = technicalActService.getAllTechnicalActs();

            // Get all specialists and their unavailable slots for today and tomorrow
            List<User> specialists = userService.getAllSpecialists();
            Map<Long, List<String>> unavailableSlotsToday = consultationService.getUnavailableSlotsForToday(specialists);
            Map<Long, List<String>> unavailableSlotsTomorrow = consultationService.getUnavailableSlotsForTomorrow(specialists);

            request.setAttribute("user", user);
            request.setAttribute("queue", queue);
            request.setAttribute("patient", patient);
            request.setAttribute("technicalActs", technicalActs);
            request.setAttribute("specialists", specialists);
            request.setAttribute("unavailableSlotsToday", unavailableSlotsToday);
            request.setAttribute("unavailableSlotsTomorrow", unavailableSlotsTomorrow);
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
        User user = userService.findById(userId);
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

            Queue queue = queueService.findById(queueId);
            if (queue == null) {
                throw new IllegalArgumentException("Queue entry not found.");
            }

            Patient patient = queue.getPatient();
            Priority priority = Priority.valueOf(priorityStr);
            List<TechnicalAct> technicalActs = consultationService.getTechnicalActsByIds(technicalActIds);

            if ("yes".equals(needSpecialist)) {
                // Scenario B: Request specialist opinion
                consultationService.validateSpecialistSelection(specialistIdStr, selectedDateTime);

                Long specialistId = Long.parseLong(specialistIdStr);
                User specialist = userService.findById(specialistId);
                if (specialist == null) {
                    throw new IllegalArgumentException("Selected specialist not found");
                }

                LocalDateTime appointmentTime = consultationService.parseDateTime(selectedDateTime);

                consultationService.createConsultationWithSpecialist(
                        patient, user, observations, priority,
                        specialist, appointmentTime, technicalActs
                );
            } else {
                // Scenario A: Generalist completes the consultation
                consultationService.createConsultationWithoutSpecialist(
                        patient, user, observations, opinion,
                        recommendations, priority, technicalActs
                );
            }

            // Remove patient from queue
            queue.delete();

            response.sendRedirect(request.getContextPath() + "/consultation");
        } catch (Exception e) {
            HttpSession errorSession = request.getSession();
            errorSession.setAttribute("error", "Error creating consultation: " + e.getMessage() + " - " + e.getClass().getSimpleName());
            response.sendRedirect(request.getContextPath() + "/queue");
        }
    }

    public void complete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.GENERALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            String consultationIdParam = request.getParameter("consultationId");
            String opinion = request.getParameter("opinion");
            String recommendations = request.getParameter("recommendations");

            if (consultationIdParam == null || consultationIdParam.isEmpty()) {
                throw new IllegalArgumentException("Consultation ID is required");
            }

            Long consultationId = Long.parseLong(consultationIdParam);
            consultationService.completeGeneralistConsultation(consultationId, opinion, recommendations);

            session.setAttribute("success", "Consultation completed successfully!");
            response.sendRedirect(request.getContextPath() + "/consultation");
        } catch (Exception e) {
            session.setAttribute("error", "Error completing consultation: " + e.getMessage());
            String consultationId = request.getParameter("consultationId");
            if (consultationId != null) {
                response.sendRedirect(request.getContextPath() + "/consultation?action=show&id=" + consultationId);
            } else {
                response.sendRedirect(request.getContextPath() + "/consultation");
            }
        }
    }

    public void cancelAndComplete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.GENERALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            String consultationIdParam = request.getParameter("consultationId");
            String cancelSpecialist = request.getParameter("cancelSpecialist");
            String opinion = request.getParameter("opinion");
            String recommendations = request.getParameter("recommendations");

            if (consultationIdParam == null || consultationIdParam.isEmpty()) {
                throw new IllegalArgumentException("Consultation ID is required");
            }

            Long consultationId = Long.parseLong(consultationIdParam);

            // Check if the checkbox was checked
            if ("true".equals(cancelSpecialist)) {
                // Cancel the specialist and complete the consultation
                consultationService.cancelAndCompleteConsultation(consultationId, opinion, recommendations);
                session.setAttribute("success", "Specialist review cancelled and consultation completed successfully!");
            } else {
                // Just redirect back if checkbox wasn't checked
                session.setAttribute("error", "No action was taken. Please check the cancel specialist checkbox to proceed.");
            }

            response.sendRedirect(request.getContextPath() + "/consultation");
        } catch (Exception e) {
            session.setAttribute("error", "Error processing consultation: " + e.getMessage());
            String consultationId = request.getParameter("consultationId");
            if (consultationId != null) {
                response.sendRedirect(request.getContextPath() + "/consultation?action=show&id=" + consultationId);
            } else {
                response.sendRedirect(request.getContextPath() + "/consultation");
            }
        }
    }
}
