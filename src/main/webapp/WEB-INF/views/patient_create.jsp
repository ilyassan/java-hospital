<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%
    User user = (User) request.getAttribute("user");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Create Patient - Medical Tele-Expertise</title>
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
        input, textarea {
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
    <h1>Create Patient</h1>
    <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
</div>

<div class="form-container">
    <h2>Patient Registration</h2>
    <% if (error != null) { %>
    <p class="error"><%= error %></p>
    <% } %>
    <form action="<%= request.getContextPath() %>/patient?action=store" method="post">
        <div class="form-group">
            <label for="cin">CIN (SSN):</label>
            <input type="text" id="cin" name="cin" required>
        </div>
        <div class="form-group">
            <label for="firstName">First Name:</label>
            <input type="text" id="firstName" name="firstName" required>
        </div>
        <div class="form-group">
            <label for="lastName">Last Name:</label>
            <input type="text" id="lastName" name="lastName" required>
        </div>
        <div class="form-group">
            <label for="dateOfBirth">Date of Birth (YYYY-MM-DD):</label>
            <input type="date" id="dateOfBirth" name="dateOfBirth" required>
        </div>
        <div class="form-group">
            <label for="allergies">Allergies:</label>
            <textarea id="allergies" name="allergies" required></textarea>
        </div>
        <div class="form-group">
            <label for="treatments">Treatments:</label>
            <textarea id="treatments" name="treatments" required></textarea>
        </div>
        <div class="form-group">
            <label for="bloodPressure">Blood Pressure:</label>
            <input type="number" step="0.1" id="bloodPressure" name="bloodPressure" required>
        </div>
        <div class="form-group">
            <label for="heartRate">Heart Rate:</label>
            <input type="number" id="heartRate" name="heartRate" required>
        </div>
        <div class="form-group">
            <label for="temperature">Temperature:</label>
            <input type="number" step="0.1" id="temperature" name="temperature" required>
        </div>
        <div class="form-group">
            <label for="respiratoryRate">Respiratory Rate:</label>
            <input type="number" id="respiratoryRate" name="respiratoryRate" required>
        </div>
        <div class="form-group">
            <label for="weight">Weight:</label>
            <input type="number" step="0.1" id="weight" name="weight" required>
        </div>
        <div class="form-group">
            <label for="height">Height:</label>
            <input type="number" step="0.1" id="height" name="height" required>
        </div>
        <button type="submit">Create Patient</button>
    </form>
</div>

<p><a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a></p>
</body>
</html>