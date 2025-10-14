<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Patient" %>
<%@ page import="com.ilyassan.medicalteleexpertise.util.CSRFUtil" %>
<%
    User user = (User) request.getAttribute("user");
    Patient patient = (Patient) request.getAttribute("patient");
    String error = (String) request.getAttribute("error");
    Boolean pendingQueue = (Boolean) request.getAttribute("pendingQueue");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Update Vital Signs - Medical Tele-Expertise</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 1000px;
            margin: 50px auto;
            padding: 20px;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        .form-container {
            background: #f4f4f4;
            padding: 20px;
            border-radius: 4px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
        }
        input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .error {
            color: red;
            margin-bottom: 10px;
        }
        .info {
            color: #2196F3;
            background-color: #E3F2FD;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .logout-btn {
            background-color: #f44336;
            color: white;
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Update Vital Signs</h1>
        <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
    </div>

    <div class="form-container">
        <h2>Update Vital Signs for <%= patient.getFirstName() %> <%= patient.getLastName() %> (CIN: <%= patient.getCin() %>)</h2>
        <% if (error != null) { %>
            <p class="error"><%= error %></p>
        <% } %>
        <% if (pendingQueue != null && pendingQueue) { %>
            <p class="info">⚠️ The patient's vital signs are more than 24 hours old. Please update them to add the patient to the queue.</p>
        <% } %>
        <form action="<%= request.getContextPath() %>/patient?action=updateVitalSigns" method="post">
            <input type="hidden" name="csrf_token" value="<%= CSRFUtil.getToken(request) %>">
            <input type="hidden" name="patientId" value="<%= patient.getId() %>">
            <div class="form-group">
                <label for="bloodPressure">Blood Pressure:</label>
                <input value="<%= patient.getBloodPressure() %>" type="number" step="0.1" id="bloodPressure" name="bloodPressure" required>
            </div>
            <div class="form-group">
                <label for="heartRate">Heart Rate:</label>
                <input value="<%= patient.getHeartRate() %>" type="number" id="heartRate" name="heartRate" required>
            </div>
            <div class="form-group">
                <label for="temperature">Temperature:</label>
                <input value="<%= patient.getTemperature() %>" type="number" step="0.1" id="temperature" name="temperature" required>
            </div>
            <div class="form-group">
                <label for="respiratoryRate">Respiratory Rate:</label>
                <input value="<%= patient.getRespiratoryRate() %>" type="number" id="respiratoryRate" name="respiratoryRate" required>
            </div>
            <div class="form-group">
                <label for="weight">Weight:</label>
                <input value="<%= patient.getWeight() %>" type="number" step="0.1" id="weight" name="weight" required>
            </div>
            <div class="form-group">
                <label for="height">Height:</label>
                <input value="<%= patient.getHeight() %>" type="number" step="0.1" id="height" name="height" required>
            </div>
            <button type="submit">Update Vital Signs</button>
        </form>
    </div>

    <p><a href="<%= request.getContextPath() %>/patient">Back to Patient List</a></p>
</body>
</html>