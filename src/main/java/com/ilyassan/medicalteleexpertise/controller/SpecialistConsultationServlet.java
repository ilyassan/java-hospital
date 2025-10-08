package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.enums.Status;
import com.ilyassan.medicalteleexpertise.model.Consultation;
import com.ilyassan.medicalteleexpertise.model.User;
import com.ilyassan.medicalteleexpertise.service.ConsultationService;
import com.ilyassan.medicalteleexpertise.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/specialist/consultations/*")
public class SpecialistConsultationServlet extends BaseServlet {

    private final ConsultationService consultationService = new ConsultationService();
    private final UserService userService = new UserService();

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.SPECIALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // Get today's agenda for this specialist
        Map<String, Consultation> timeSlotConsultations = consultationService.getSpecialistTodayAgenda(userId);
        List<String> allTimeSlots = consultationService.generateAllTimeSlots();

        request.setAttribute("user", user);
        request.setAttribute("timeSlotConsultations", timeSlotConsultations);
        request.setAttribute("allTimeSlots", allTimeSlots);
        view(request, response, "specialist_consultations_agenda.jsp");
    }

    public void show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.SPECIALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String consultationIdParam = request.getParameter("id");
        if (consultationIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/specialist/consultations");
            return;
        }

        try {
            Long consultationId = Long.parseLong(consultationIdParam);
            Consultation consultation = consultationService.findById(consultationId);

            if (consultation == null) {
                request.setAttribute("error", "Consultation not found.");
                response.sendRedirect(request.getContextPath() + "/specialist/consultations");
                return;
            }

            // Verify this consultation belongs to this specialist
            if (consultation.getSpecialist() == null || !consultation.getSpecialist().getId().equals(userId)) {
                request.setAttribute("error", "You don't have access to this consultation.");
                response.sendRedirect(request.getContextPath() + "/specialist/consultations");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("consultation", consultation);
            view(request, response, "specialist_consultation_detail.jsp");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/specialist/consultations");
        }
    }

    public void submit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.SPECIALIST) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            Long consultationId = Long.parseLong(request.getParameter("consultationId"));
            String opinion = request.getParameter("opinion");
            String recommendations = request.getParameter("recommendations");

            if (opinion == null || opinion.trim().isEmpty() ||
                recommendations == null || recommendations.trim().isEmpty()) {
                throw new IllegalArgumentException("Opinion and Recommendations are required.");
            }

            Consultation consultation = consultationService.findById(consultationId);
            if (consultation == null) {
                throw new IllegalArgumentException("Consultation not found.");
            }

            // Verify this consultation belongs to this specialist
            if (consultation.getSpecialist() == null || !consultation.getSpecialist().getId().equals(userId)) {
                throw new IllegalArgumentException("You don't have access to this consultation.");
            }

            // Update consultation with opinion and recommendations, and mark as completed
            consultationService.completeSpecialistConsultation(consultationId, opinion, recommendations);

            response.sendRedirect(request.getContextPath() + "/specialist/consultations");
        } catch (Exception e) {
            HttpSession errorSession = request.getSession();
            errorSession.setAttribute("error", "Error submitting consultation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/specialist/consultations");
        }
    }
}
