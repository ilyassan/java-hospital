package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.Queue;
import com.ilyassan.medicalteleexpertise.model.User;
import com.ilyassan.medicalteleexpertise.service.PatientService;
import com.ilyassan.medicalteleexpertise.service.QueueService;
import com.ilyassan.medicalteleexpertise.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/queue")
public class QueueServlet extends BaseServlet {

    private final QueueService queueService = new QueueService();
    private final UserService userService = new UserService();
    private final PatientService patientService = new PatientService();

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || (user.getRole() != Role.NURSE
                && user.getRole() != Role.GENERALIST)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        List<Queue> queues = queueService.getAllQueuesSortedByArrival();

        String error = (String) session.getAttribute("error");
        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("error");
        }

        request.setAttribute("queues", queues);
        request.setAttribute("user", user);
        view(request, response, "queue_list.jsp");
    }

    public void store(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.NURSE) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            Long patientId = Long.parseLong(request.getParameter("patientId"));
            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                request.setAttribute("error", "Patient not found.");
                index(request, response);
                return;
            }

            if (queueService.isPatientInQueue(patientId)) {
                request.setAttribute("error", "Patient is already in the queue.");
                index(request, response);
                return;
            }

            queueService.addPatientToQueue(patient);

            response.sendRedirect(request.getContextPath() + "/patient");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid patient ID.");
            index(request, response);
        }
    }
}