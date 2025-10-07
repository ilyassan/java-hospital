package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Status;
import com.ilyassan.medicalteleexpertise.model.Patient;
import com.ilyassan.medicalteleexpertise.model.Queue;
import com.ilyassan.medicalteleexpertise.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/patient")
public class PatientServlet extends BaseServlet {

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = User.find(userId);
        if (user == null || user.getRole() != com.ilyassan.medicalteleexpertise.enums.Role.NURSE) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        List<Patient> patients = Patient.all();
        request.setAttribute("patients", patients);
        request.setAttribute("user", user);
        view(request, response, "patient_list.jsp");
    }

    public void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = User.find(userId);
        if (user == null || user.getRole() != com.ilyassan.medicalteleexpertise.enums.Role.NURSE) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.setAttribute("user", user);
        view(request, response, "patient_create.jsp");
    }

    public void store(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = User.find(userId);
        if (user == null || user.getRole() != com.ilyassan.medicalteleexpertise.enums.Role.NURSE) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            // Validate required fields
            String cin = request.getParameter("cin");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String dateOfBirth = request.getParameter("dateOfBirth");
            String allergies = request.getParameter("allergies");
            String treatments = request.getParameter("treatments");
            String bloodPressure = request.getParameter("bloodPressure");
            String heartRate = request.getParameter("heartRate");
            String temperature = request.getParameter("temperature");
            String respiratoryRate = request.getParameter("respiratoryRate");
            String weight = request.getParameter("weight");
            String height = request.getParameter("height");

            if (isEmpty(cin) || isEmpty(firstName) || isEmpty(lastName) || isEmpty(dateOfBirth) ||
                    isEmpty(allergies) || isEmpty(treatments) ||
                    isEmpty(bloodPressure) || isEmpty(heartRate) || isEmpty(temperature) ||
                    isEmpty(respiratoryRate) || isEmpty(weight) || isEmpty(height)) {
                request.setAttribute("error", "All fields are required.");
                request.setAttribute("user", user);
                view(request, response, "patient_create.jsp");
                return;
            }

            Patient patient = new Patient();
            patient.setCin(cin);
            patient.setFirstName(firstName);
            patient.setLastName(lastName);
            patient.setDateOfBirth(LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE));
            patient.setAllergies(allergies);
            patient.setTreatments(treatments);
            patient.setBloodPressure(Double.parseDouble(bloodPressure));
            patient.setHeartRate(Integer.parseInt(heartRate));
            patient.setTemperature(Double.parseDouble(temperature));
            patient.setRespiratoryRate(Integer.parseInt(respiratoryRate));
            patient.setWeight(Double.parseDouble(weight));
            patient.setHeight(Double.parseDouble(height));
            patient.setVitalSignsTimestamp(LocalDateTime.now());
            patient.create();

            response.sendRedirect(request.getContextPath() + "/patient");
        } catch (DateTimeParseException | NumberFormatException e) {
            request.setAttribute("error", "Invalid input format. Please check your entries.");
            request.setAttribute("user", user);
            view(request, response, "patient_create.jsp");
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Double parseDouble(String value) {
        return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
    }

    private Integer parseInteger(String value) {
        return value != null && !value.isEmpty() ? Integer.parseInt(value) : null;
    }
}