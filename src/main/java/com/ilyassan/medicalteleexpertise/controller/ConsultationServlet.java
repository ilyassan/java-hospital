package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Status;
import com.ilyassan.medicalteleexpertise.model.Consultation;
import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.Queue;
import com.ilyassan.medicalteleexpertise.model.TechnicalAct;
import com.ilyassan.medicalteleexpertise.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/consultation/*")
public class ConsultationServlet extends BaseServlet {

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = User.find(userId);
        if (user == null || user.getRole() != com.ilyassan.medicalteleexpertise.enums.Role.GENERALIST) {
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
        if (user == null || user.getRole() != com.ilyassan.medicalteleexpertise.enums.Role.GENERALIST) {
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

            request.setAttribute("user", user);
            request.setAttribute("queue", queue);
            request.setAttribute("patient", patient);
            request.setAttribute("technicalActs", technicalActs);
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
        if (user == null || user.getRole() != com.ilyassan.medicalteleexpertise.enums.Role.GENERALIST) {
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
            com.ilyassan.medicalteleexpertise.enums.Priority priority =
                com.ilyassan.medicalteleexpertise.enums.Priority.valueOf(priorityStr);
            consultation.setPriority(priority);

            // Determine status based on needSpecialist choice
            if ("yes".equals(needSpecialist)) {
                // Scenario B: Request specialist opinion (opinion/recommendations will be added by specialist)
                consultation.setStatus(Status.PENDING_SPECIALIST_OPINION);
            } else {
                // Scenario A: Generalist completes the consultation
                consultation.setOpinion(opinion);
                consultation.setRecommendations(recommendations);
                consultation.setStatus(Status.COMPLETED);
            }

            // Calculate cost: Fixed 150 DH + technical acts
            double totalCost = 150.0;

            // Add technical acts if selected
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

            consultation.setCost(totalCost);
            consultation.create();

            // Remove patient from queue
            queue.delete();

            response.sendRedirect(request.getContextPath() + "/consultation");
        } catch (Exception e) {
            request.setAttribute("error", "Error creating consultation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/queue");
        }
    }
}
