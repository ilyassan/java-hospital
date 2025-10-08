package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.model.User;
import com.ilyassan.medicalteleexpertise.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends BaseServlet {

    private final UserService userService = new UserService();

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);

        if (user == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("user", user);
        view(request, response, "dashboard.jsp");
    }
}
