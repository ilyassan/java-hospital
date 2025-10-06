<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home - Medical Tele-Expertise</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        h1 {
            color: #333;
        }
        .links {
            margin-top: 30px;
        }
        .links a {
            display: inline-block;
            margin-right: 20px;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <h1>Welcome to Medical Tele-Expertise System</h1>
    <p>This is a web application for medical tele-expertise, facilitating coordination between general practitioners and specialists for remote medical collaboration.</p>

    <div class="links">
        <% if (session.getAttribute("userId") != null) { %>
            <a href="<%= request.getContextPath() %>/dashboard">Go to Dashboard</a>
        <% } else { %>
            <a href="<%= request.getContextPath() %>/login">Login</a>
        <% } %>
    </div>
</body>
</html>
