<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Patient" %>
<%@ page import="java.util.List" %>
<%
  User user = (User) request.getAttribute("user");
  List<Patient> patients = (List<Patient>) request.getAttribute("patients");
  String error = (String) request.getAttribute("error");
  Boolean searchPerformed = (Boolean) request.getAttribute("searchPerformed");
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
    .table-container {
      overflow-x: auto;
      margin-top: 20px;
    }
    table {
      width: 100%;
      min-width: 900px;
      border-collapse: collapse;
    }
    th, td {
      border: 1px solid #ddd;
      padding: 8px;
      text-align: left;
    }
    th {
      background-color: #f2f2f2;
    }
    th:nth-child(1), td:nth-child(1) { /* CIN */
      width: 15%;
    }
    th:nth-child(2), td:nth-child(2) { /* First Name */
      width: 15%;
    }
    th:nth-child(3), td:nth-child(3) { /* Last Name */
      width: 15%;
    }
    th:nth-child(4), td:nth-child(4) { /* Date of Birth */
      width: 10%;
    }
    th:nth-child(5), td:nth-child(5) { /* Allergies */
      width: 20%;
    }
    th:nth-child(6), td:nth-child(6) { /* Vital Signs */
      width: 25%;
    }
    th:nth-child(7), td:nth-child(7) { /* Actions */
      width: 15%;
    }
    .action-btn {
      background-color: #4CAF50;
      color: white;
      padding: 6px 12px;
      text-decoration: none;
      border-radius: 4px;
      margin-right: 5px;
    }
    .search-form {
      margin-bottom: 20px;
    }
    .search-form input {
      padding: 8px;
      border: 1px solid #ddd;
      border-radius: 4px;
      width: 200px;
    }
    .search-form button {
      background-color: #4CAF50;
      color: white;
      padding: 8px 16px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    .error {
      color: red;
      margin-bottom: 10px;
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
  <h3>Patient Management</h3>
  <div class="search-form">
    <form action="<%= request.getContextPath() %>/patient" method="get">
      <input type="text" name="cin" placeholder="Search by CIN" required>
      <button type="submit">Search</button>
    </form>
  </div>
  <a href="<%= request.getContextPath() %>/patient?action=create" class="action-btn">Create New Patient</a>
  <% if (error != null) { %>
  <p class="error"><%= error %></p>
  <% } %>
  <div class="table-container">
    <table>
      <thead>
      <tr>
        <th>CIN</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Date of Birth</th>
        <th>Allergies</th>
        <th>Vital Signs</th>
        <th>Actions</th>
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
        <td>
          <a href="<%= request.getContextPath() %>/patient?action=updateVitalSignsForm&patientId=<%= patient.getId() %>" class="action-btn">Update</a>
        </td>
      </tr>
      <% } %>
      <% } else { %>
      <tr>
        <td colspan="7">No patients found.</td>
      </tr>
      <% } %>
      </tbody>
    </table>
  </div>
</div>

<p><a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a></p>
</body>
</html>