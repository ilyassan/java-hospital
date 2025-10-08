package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.Queue;
import com.ilyassan.medicalteleexpertise.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/queue")
public class QueueServlet extends BaseServlet {

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = User.find(userId);
        if (user == null || (user.getRole() != Role.NURSE
                && user.getRole() != Role.GENERALIST)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        List<Queue> queues = Queue.all().stream()
                .sorted((q1, q2) -> q1.getArrivalTime().compareTo(q2.getArrivalTime()))
                .collect(Collectors.toList());

        // Check if there's an error in session (from redirect)
        String error = (String) session.getAttribute("error");
        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("error"); // Clear it after reading
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
        User user = User.find(userId);
        if (user == null || user.getRole() != Role.NURSE) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            Long patientId = Long.parseLong(request.getParameter("patientId"));
            Patient patient = Patient.find(patientId);
            if (patient == null) {
                request.setAttribute("error", "Patient not found.");
                index(request, response);
                return;
            }

            boolean isAlreadyInQueue = Queue.all().stream()
                    .anyMatch(q -> q.getPatient().getId().equals(patientId));

            if (isAlreadyInQueue) {
                request.setAttribute("error", "Patient is already in the queue.");
                index(request, response);
                return;
            }

            Queue queue = new Queue();
            queue.setPatient(patient);
            queue.create();

            response.sendRedirect(request.getContextPath() + "/patient");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid patient ID.");
            index(request, response);
        }
    }
}