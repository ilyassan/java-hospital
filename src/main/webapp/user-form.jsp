<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>User Management</title>
</head>
<body>

    <c:choose>
        <c:when test="${user != null}">
            <form action="users?action=update" method="post">
            <h1>Edit User</h1>
        </c:when>
        <c:otherwise>
            <form action="users?action=store" method="post">
            <h1>Add New User</h1>
        </c:otherwise>
    </c:choose>

        <table border="1" cellpadding="5">
            <c:if test="${user != null}">
                <input type="hidden" name="id" value="<c:out value='${user.id}' />" />
            </c:if>
            <tr>
                <th>Name:</th>
                <td><input type="text" name="name" size="45" value="${user != null ? user.name : ''}" /></td>
            </tr>
            <tr>
                <th>Email:</th>
                <td><input type="text" name="email" size="45" value="${user != null ? user.email : ''}" /></td>
            </tr>
            <tr>
                <td colspan="2" align="center"><input type="submit" value="Save" /></td>
            </tr>
        </table>
    </form>
</body>
</html>