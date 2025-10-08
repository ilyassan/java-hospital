<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%
    User user = (User) request.getAttribute("user");
    Double revenue = (Double) request.getAttribute("revenue");
    Long numberOfConsultations = (Long) request.getAttribute("numberOfConsultations");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Statistics - Medical Tele-Expertise</title>
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
        .content {
            margin-top: 20px;
        }
        .stat-card {
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .stat-card h3 {
            margin-top: 0;
        }
        .stat-card p {
            font-size: 24px;
            font-weight: bold;
            margin: 10px 0;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>Statistics</h1>
    <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
</div>

<div class="user-info">
    <h2>Welcome, <%= user.getFirstName() %> <%= user.getLastName() %></h2>
    <p><strong>Email:</strong> <%= user.getEmail() %></p>
    <p><strong>Role:</strong> <%= user.getRole() %></p>
</div>

<div class="content">
    <h3>Your Performance Statistics</h3>
    <div class="stat-card">
        <h3>Total Consultations</h3>
        <p><%= numberOfConsultations %></p>
    </div>
    <div class="stat-card">
        <h3>Total Income</h3>
        <p><%= revenue %> DH</p>
    </div>
</div>

<p><a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a></p>
</body>
</html>