<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Consultation" %>
<%@ page import="com.ilyassan.medicalteleexpertise.enums.Status" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    User user = (User) request.getAttribute("user");
    Map<String, Consultation> timeSlotConsultations = (Map<String, Consultation>) request.getAttribute("timeSlotConsultations");
    List<String> allTimeSlots = (List<String>) request.getAttribute("allTimeSlots");
%>
<!DOCTYPE html>
<html>
<head>
    <title>My Consultations Agenda - Medical Tele-Expertise</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .time-slot {
            padding: 15px;
            text-align: center;
            border: 2px solid #ccc;
            border-radius: 4px;
            background-color: #f5f5f5;
            color: #999;
            min-height: 80px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .time-slot.pending {
            border-color: #ff9800;
            background-color: #fff3e0;
            color: #e65100;
            cursor: pointer;
            transition: all 0.3s;
        }
        .time-slot.pending:hover {
            background-color: #ffe0b2;
            transform: scale(1.05);
        }
        .time-slot.completed {
            border-color: #4CAF50;
            background-color: #e8f5e9;
            color: #2e7d32;
            cursor: pointer;
            transition: all 0.3s;
        }
        .time-slot.completed:hover {
            background-color: #c8e6c9;
            transform: scale(1.05);
        }
        .time-slot .time { font-size: 16px; font-weight: bold; }
        .time-slot .status { font-size: 12px; margin-top: 5px; }
        .time-slot .patient { font-size: 13px; margin-top: 5px; font-weight: 500; }
    </style>
</head>
<body>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>My Consultations Agenda - Today</h1>
        <a href="<%= request.getContextPath() %>/logout" class="btn btn-danger">Logout</a>
    </div>

    <div class="card mb-4">
        <div class="card-header bg-secondary text-white">
            <h5 class="mb-0">Dr. <%= user.getFirstName() %> <%= user.getLastName() %></h5>
        </div>
        <div class="card-body">
            <p class="mb-1"><strong>Specialty:</strong> <%= user.getSpecialty() %></p>
            <p class="mb-1"><strong>Email:</strong> <%= user.getEmail() %></p>
            <% if (user.getPhone() != null) { %>
            <p class="mb-0"><strong>Phone:</strong> <%= user.getPhone() %></p>
            <% } %>
        </div>
    </div>

    <div class="alert alert-info d-flex justify-content-around">
        <div><span class="badge bg-secondary">Gray</span> Available / No Consultation</div>
        <div><span class="badge bg-warning text-dark">Orange</span> Pending Opinion (Click to view)</div>
        <div><span class="badge bg-success">Green</span> Completed (Click to view)</div>
    </div>

    <h4 class="mt-4 mb-3">Morning (8:00 AM - 12:00 PM)</h4>
    <div class="row row-cols-2 row-cols-md-4 row-cols-lg-6 g-3 mb-4">
        <%
        for (String timeSlot : allTimeSlots) {
            String[] parts = timeSlot.split(":");
            int hour = Integer.parseInt(parts[0]);

            if (hour >= 8 && hour < 12) {
                Consultation consultation = timeSlotConsultations.get(timeSlot);
                String slotClass = "time-slot";
                String statusText = "";
                String patientInfo = "";
                String clickAction = "";

                if (consultation != null) {
                    if (consultation.getStatus() == Status.COMPLETED) {
                        slotClass += " completed";
                        statusText = "Completed";
                    } else {
                        slotClass += " pending";
                        statusText = "Pending";
                    }
                    patientInfo = consultation.getPatient().getFirstName() + " " +
                                  consultation.getPatient().getLastName().charAt(0) + ".";
                    clickAction = "onclick=\"window.location.href='" + request.getContextPath() +
                                  "/specialist/consultations?action=show&id=" + consultation.getId() + "'\"";
                }
        %>
        <div class="col">
            <div class="<%= slotClass %>" <%= clickAction %>>
                <div class="time"><%= timeSlot %></div>
                <% if (consultation != null) { %>
                <div class="patient"><%= patientInfo %></div>
                <div class="status"><%= statusText %></div>
                <% } %>
            </div>
        </div>
        <%
            }
        }
        %>
    </div>

    <h4 class="mt-4 mb-3">Afternoon (2:00 PM - 6:00 PM)</h4>
    <div class="row row-cols-2 row-cols-md-4 row-cols-lg-6 g-3 mb-4">
        <%
        for (String timeSlot : allTimeSlots) {
            String[] parts = timeSlot.split(":");
            int hour = Integer.parseInt(parts[0]);

            if (hour >= 14 && hour < 18) {
                Consultation consultation = timeSlotConsultations.get(timeSlot);
                String slotClass = "time-slot";
                String statusText = "";
                String patientInfo = "";
                String clickAction = "";

                if (consultation != null) {
                    if (consultation.getStatus() == Status.COMPLETED) {
                        slotClass += " completed";
                        statusText = "Completed";
                    } else {
                        slotClass += " pending";
                        statusText = "Pending";
                    }
                    patientInfo = consultation.getPatient().getFirstName() + " " +
                                  consultation.getPatient().getLastName().charAt(0) + ".";
                    clickAction = "onclick=\"window.location.href='" + request.getContextPath() +
                                  "/specialist/consultations?action=show&id=" + consultation.getId() + "'\"";
                }
        %>
        <div class="col">
            <div class="<%= slotClass %>" <%= clickAction %>>
                <div class="time"><%= timeSlot %></div>
                <% if (consultation != null) { %>
                <div class="patient"><%= patientInfo %></div>
                <div class="status"><%= statusText %></div>
                <% } %>
            </div>
        </div>
        <%
            }
        }
        %>
    </div>

    <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-link">← Back to Dashboard</a>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
