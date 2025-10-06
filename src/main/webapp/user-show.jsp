<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>User Details</title>
</head>
<body>
    <h1>User Details</h1>
    
    <table>
        <tr>
            <th>ID: </th>
            <td><c:out value="${user.id}" /></td>
        </tr>
        <tr>
            <th>Name: </th>
            <td><c:out value="${user.name}" /></td>
        </tr>
        <tr>
            <th>Email: </th>
            <td><c:out value="${user.email}" /></td>
        </tr>
    </table>
    <br/>
    <a href="users">Back to User List</a>
</body>
</html>
