<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.enums.Role" %>
<%
    User user = (User) request.getAttribute("user");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Medical Tele-Expertise</title>
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
        .content a {
            display: inline-block;
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Dashboard</h1>
        <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
    </div>

    <div class="user-info">
        <h2>Welcome, <%= user.getFirstName() %> <%= user.getLastName() %></h2>
        <p><strong>Email:</strong> <%= user.getEmail() %></p>
        <p><strong>Role:</strong> <%= user.getRole() %></p>
        <% if (user.getPhone() != null) { %>
            <p><strong>Phone:</strong> <%= user.getPhone() %></p>
        <% } %>
        <% if (user.getSpecialty() != null) { %>
            <p><strong>Specialty:</strong> <%= user.getSpecialty() %></p>
        <% } %>
        <% if (user.getTariff() != null) { %>
            <p><strong>Tariff:</strong> <%= user.getTariff() %> DH</p>
        <% } %>
    </div>

    <div class="content">
        <h3>Your Features</h3>
        <% if (user.getRole() == Role.NURSE) { %>
            <p>Register a new patient or search for an existing one.</p>
            <a href="<%= request.getContextPath() %>/patient">Create New Patient</a>
        <% } else { %>
            <p>Based on your role (<%= user.getRole() %>), you will see specific features and functionalities.</p>
        <% } %>
    </div>

    <p><a href="<%= request.getContextPath() %>/">Back to Home</a></p>
</body>
</html>