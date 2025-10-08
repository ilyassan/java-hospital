<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Patient" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Queue" %>
<%@ page import="java.util.List" %>
<%
  User user = (User) request.getAttribute("user");
  List<Queue> queues = (List<Queue>) request.getAttribute("queues");
  String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
  <title>Queue List - Medical Tele-Expertise</title>
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
    ol {
      padding-left: 20px;
    }
    li {
      margin-bottom: 15px;
      padding: 10px;
      background: #f9f9f9;
      border: 1px solid #ddd;
      border-radius: 4px;
    }
    .error {
      color: red;
      margin-bottom: 10px;
    }
    .consult-btn {
      background-color: #4CAF50;
      color: white;
      padding: 8px 16px;
      text-decoration: none;
      border-radius: 4px;
      display: inline-block;
      margin-top: 10px;
    }
    .consult-btn:hover {
      background-color: #45a049;
    }
  </style>
</head>
<body>
<div class="header">
  <h1>Patients in Queue</h1>
  <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
</div>

<div class="user-info">
  <h2>Welcome, <%= user.getFirstName() %> <%= user.getLastName() %></h2>
  <p><strong>Email:</strong> <%= user.getEmail() %></p>
  <p><strong>Role:</strong> <%= user.getRole() %></p>
</div>

<div class="content">
  <h3>Waiting Patients (Sorted by Arrival Time)</h3>
  <% if (error != null) { %>
  <p class="error"><%= error %></p>
  <% } %>
  <% if (queues != null && !queues.isEmpty()) { %>
  <ol>
    <%
      boolean isFirst = true;
      for (Queue queue : queues) {
      Patient patient = queue.getPatient();
    %>
    <li>
      <strong>CIN:</strong> <%= patient.getCin() %><br>
      <strong>Name:</strong> <%= patient.getFirstName() %> <%= patient.getLastName() %><br>
      <strong>Arrival Time:</strong> <%= queue.getArrivalTime() %><br>
      <strong>Vital Signs:</strong>
      <% if (patient.getVitalSignsTimestamp() != null) { %>
      BP: <%= patient.getBloodPressure() != null ? patient.getBloodPressure() : "N/A" %>,
      HR: <%= patient.getHeartRate() != null ? patient.getHeartRate() : "N/A" %>,
      Temp: <%= patient.getTemperature() != null ? patient.getTemperature() : "N/A" %>,
      RR: <%= patient.getRespiratoryRate() != null ? patient.getRespiratoryRate() : "N/A" %>,
      Weight: <%= patient.getWeight() != null ? patient.getWeight() : "N/A" %>,
      Height: <%= patient.getHeight() != null ? patient.getHeight() : "N/A" %>
      <% } else { %>
      No vital signs recorded
      <% } %>
      <% if (user.getRole() == com.ilyassan.medicalteleexpertise.enums.Role.GENERALIST && isFirst) { %>
      <br><br>
      <a href="<%= request.getContextPath() %>/consultation?action=create&queueId=<%= queue.getId() %>" class="consult-btn">Consult</a>
      <% } %>
    </li>
    <%
        isFirst = false;
      }
    %>
  </ol>
  <% } else { %>
  <p>No patients in the queue.</p>
  <% } %>
</div>

<p><a href="<%= request.getContextPath() %>/patient">Back to Patient List</a></p>
<p><a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a></p>
</body>
</html>