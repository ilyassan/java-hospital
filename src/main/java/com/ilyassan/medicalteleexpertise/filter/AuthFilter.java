package com.ilyassan.medicalteleexpertise.filter;

import com.ilyassan.medicalteleexpertise.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/dashboard")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Verify token in session matches token in database
        Long userId = (Long) session.getAttribute("userId");
        String sessionToken = (String) session.getAttribute("token");

        User user = User.find(userId);

        if (user == null || !sessionToken.equals(user.getToken())) {
            session.invalidate();
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // User is authenticated, continue
        chain.doFilter(request, response);
    }
}
