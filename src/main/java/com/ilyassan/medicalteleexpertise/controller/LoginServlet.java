package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.model.User;
import com.ilyassan.medicalteleexpertise.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // If user is already authenticated, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        view(request, response, "login.jsp");
    }

    public void authenticate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required");
            view(request, response, "login.jsp");
            return;
        }

        // Find user by email
        List<User> users = User.all();
        User user = users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        // Validate password with BCrypt
        if (user == null || !PasswordUtil.verifyPassword(password, user.getPassword())) {
            request.setAttribute("error", "Invalid email or password");
            view(request, response, "login.jsp");
            return;
        }

        // Generate token and save to database
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.update();

        // Create session
        HttpSession session = request.getSession();
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole().name());
        session.setAttribute("token", token);

        // Redirect to dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
