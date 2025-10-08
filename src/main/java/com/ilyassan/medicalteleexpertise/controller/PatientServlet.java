package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.model.Patient;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@WebServlet("/patient")
public class PatientServlet extends BaseServlet {

    private final PatientService patientService = new PatientService();
    private final UserService userService = new UserService();
    private final QueueService queueService = new QueueService();

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        String cin = request.getParameter("cin");
        List<Patient> patients;
        if (cin != null && !cin.trim().isEmpty()) {
            Patient patient = patientService.findByCin(cin.trim());
            if (patient != null) {
                patients = List.of(patient);
                request.setAttribute("searchPerformed", true);
            } else {
                patients = patientService.getAllPatients();
                request.setAttribute("error", "No patient found with CIN: " + cin);
            }
        } else {
            patients = patientService.getAllPatients();
        }

        Set<Long> inQueueIds = queueService.getPatientIdsInQueue();
        request.setAttribute("inQueueIds", inQueueIds);

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
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.NURSE) {
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
        User user = userService.findById(userId);
        if (user == null || user.getRole() != Role.NURSE) {
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
            patientService.createPatient(patient);

            response.sendRedirect(request.getContextPath() + "/patient");
        } catch (DateTimeParseException | NumberFormatException e) {
            request.setAttribute("error", "Invalid input format. Please check your entries.");
            request.setAttribute("user", user);
            view(request, response, "patient_create.jsp");
        }
    }

    public void updateVitalSignsForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        String patientId = request.getParameter("patientId");
        Patient patient = patientService.findById(Long.parseLong(patientId));
        if (patient == null) {
            request.setAttribute("error", "Patient not found.");
            index(request, response);
            return;
        }

        request.setAttribute("patient", patient);
        request.setAttribute("user", user);
        view(request, response, "patient_vital_signs.jsp");
    }

    public void updateVitalSigns(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            String patientId = request.getParameter("patientId");
            Patient patient = Patient.find(Long.parseLong(patientId));
            if (patient == null) {
                request.setAttribute("error", "Patient not found.");
                index(request, response);
                return;
            }

            String bloodPressure = request.getParameter("bloodPressure");
            String heartRate = request.getParameter("heartRate");
            String temperature = request.getParameter("temperature");
            String respiratoryRate = request.getParameter("respiratoryRate");
            String weight = request.getParameter("weight");
            String height = request.getParameter("height");

            if (isEmpty(bloodPressure) || isEmpty(heartRate) || isEmpty(temperature) ||
                    isEmpty(respiratoryRate) || isEmpty(weight) || isEmpty(height)) {
                request.setAttribute("error", "All vital signs fields are required.");
                request.setAttribute("patient", patient);
                request.setAttribute("user", user);
                view(request, response, "patient_vital_signs.jsp");
                return;
            }

            patient.setBloodPressure(Double.parseDouble(bloodPressure));
            patient.setHeartRate(Integer.parseInt(heartRate));
            patient.setTemperature(Double.parseDouble(temperature));
            patient.setRespiratoryRate(Integer.parseInt(respiratoryRate));
            patient.setWeight(Double.parseDouble(weight));
            patient.setHeight(Double.parseDouble(height));
            patient.setVitalSignsTimestamp(LocalDateTime.now());
            patientService.updatePatient(patient);

            response.sendRedirect(request.getContextPath() + "/patient");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid input format for vital signs.");
            request.setAttribute("patient", patientService.findById(Long.parseLong(request.getParameter("patientId"))));
            request.setAttribute("user", user);
            view(request, response, "patient_vital_signs.jsp");
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}