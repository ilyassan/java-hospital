package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            // Clear token from database
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                User user = User.find(userId);
                if (user != null) {
                    user.setToken(null);
                    user.update();
                }
            }

            // Invalidate session
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/login");
    }
}
