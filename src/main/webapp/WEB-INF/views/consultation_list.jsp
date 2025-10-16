<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Consultation" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) request.getAttribute("user");
    List<Consultation> consultations = (List<Consultation>) request.getAttribute("consultations");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Consultations - Medical Tele-Expertise</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 1200px;
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
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        .status-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }
        .status-pending {
            background-color: #fff3cd;
            color: #856404;
        }
        .status-completed {
            background-color: #d4edda;
            color: #155724;
        }
        .status-pending-specialist {
            background-color: #d1ecf1;
            color: #0c5460;
        }
        .view-btn {
            background-color: #2196F3;
            color: white;
            padding: 6px 12px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
        }
        .view-btn:hover {
            background-color: #0b7dda;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>Consultations</h1>
    <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
</div>

<div class="user-info">
    <h2>Welcome, <%= user.getFirstName() %> <%= user.getLastName() %></h2>
    <p><strong>Email:</strong> <%= user.getEmail() %></p>
    <p><strong>Role:</strong> <%= user.getRole() %></p>
</div>

<%
    String successMessage = (String) session.getAttribute("success");
    String errorMessage = (String) session.getAttribute("error");
    if (successMessage != null) {
        session.removeAttribute("success");
%>
<div class="alert alert-success"><%= successMessage %></div>
<%
    }
    if (errorMessage != null) {
        session.removeAttribute("error");
%>
<div class="alert alert-error"><%= errorMessage %></div>
<%
    }
%>

<div class="content">
    <h3>All Consultations</h3>
    <% if (consultations != null && !consultations.isEmpty()) { %>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Patient</th>
                <th>CIN</th>
                <th>Meet Link</th>
                <th>Observations</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Cost (DH)</th>
                <th>Created At</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% for (Consultation consultation : consultations) { %>
            <tr>
                <td>#<%= consultation.getId() %></td>
                <td><%= consultation.getPatient().getFirstName() %> <%= consultation.getPatient().getLastName() %></td>
                <td><%= consultation.getPatient().getCin() %></td>
                <% if(consultation.getMeetLink() == null){ %>
                <td><%= "Unavailable" %></td>
                <% } else { %>
                <td><a href="<%= consultation.getMeetLink() %>">Enter the meet</a></td>
                <% } %>
                <td><%= consultation.getObservations().length() > 50 ? consultation.getObservations().substring(0, 50) + "..." : consultation.getObservations() %></td>
                <td><%= consultation.getPriority() %></td>
                <td>
                    <%
                        String statusClass = "";
                        String statusText = "";
                        switch (consultation.getStatus()) {
                            case COMPLETED:
                                statusClass = "status-completed";
                                statusText = "Completed";
                                break;
                            case PENDING_SPECIALIST_OPINION:
                                statusClass = "status-pending-specialist";
                                statusText = "Pending Specialist";
                                break;
                        }
                    %>
                    <span class="status-badge <%= statusClass %>"><%= statusText %></span>
                </td>
                <td><%= consultation.getCost() != null ? String.format("%.2f", consultation.getCost()) : "N/A" %></td>
                <td><%= consultation.getCreatedAt() != null ? consultation.getCreatedAt().toString() : "N/A" %></td>
                <td>
                    <a href="<%= request.getContextPath() %>/consultation?action=show&id=<%= consultation.getId() %>" class="view-btn">View Details</a>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } else { %>
    <p>No consultations found.</p>
    <% } %>
</div>

<p><a href="<%= request.getContextPath() %>/queue">View Queue</a></p>
<p><a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a></p>
</body>
</html>
