<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Patient" %>
<%@ page import="java.util.List" %>
<%
  User user = (User) request.getAttribute("user");
  List<Patient> patients = (List<Patient>) request.getAttribute("patients");
%>
<!DOCTYPE html>
<html>
<head>
  <title>Patient List - Medical Tele-Expertise</title>
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
    .user-info {
      background: #f4f4f4;
      padding: 15px;
      border-radius: 4px;
      margin-bottom: 20px;
    }
    .logout-btn {
      background-color: #f44336;
      color: white;
      padding: 8px 16px;
      text-decoration: none;
      border-radius: 4px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
    }
    th, td {
      border: 1px solid #ddd;
      padding: 8px;
      text-align: left;
    }
    th {
      background-color: #f2f2f2;
    }
    .action-btn {
      background-color: #4CAF50;
      color: white;
      padding: 6px 12px;
      text-decoration: none;
      border-radius: 4px;
    }
  </style>
</head>
<body>
<div class="header">
  <h1>Patient List</h1>
  <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
</div>

<div class="user-info">
  <h2>Welcome, <%= user.getFirstName() %> <%= user.getLastName() %></h2>
  <p><strong>Email:</strong> <%= user.getEmail() %></p>
  <p><strong>Role:</strong> <%= user.getRole() %></p>
</div>

<div class="content">
  <h3>All Patients</h3>
  <a href="<%= request.getContextPath() %>/patient?action=create" class="action-btn">Create New Patient</a>
  <table>
    <thead>
    <tr>
      <th>CIN</th>
      <th>First Name</th>
      <th>Last Name</th>
      <th>Date of Birth</th>
      <th>Allergies</th>
      <th>Vital Signs</th>
    </tr>
    </thead>
    <tbody>
    <% if (patients != null && !patients.isEmpty()) { %>
    <% for (Patient patient : patients) { %>
    <tr>
      <td><%= patient.getCin() %></td>
      <td><%= patient.getFirstName() %></td>
      <td><%= patient.getLastName() %></td>
      <td><%= patient.getDateOfBirth() %></td>
      <td><%= patient.getAllergies() != null ? patient.getAllergies() : "None" %></td>
      <td>
        <% if (patient.getVitalSignsTimestamp() != null) { %>
        BP: <%= patient.getBloodPressure() != null ? patient.getBloodPressure() : "N/A" %>,
        HR: <%= patient.getHeartRate() != null ? patient.getHeartRate() : "N/A" %>,
        Temp: <%= patient.getTemperature() != null ? patient.getTemperature() : "N/A" %>,
        RR: <%= patient.getRespiratoryRate() != null ? patient.getRespiratoryRate() : "N/A" %>
        <% } else { %>
        No vital signs recorded
        <% } %>
      </td>
    </tr>
    <% } %>
    <% } else { %>
    <tr>
      <td colspan="6">No patients found.</td>
    </tr>
    <% } %>
    </tbody>
  </table>
</div>

<p><a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a></p>
</body>
</html>