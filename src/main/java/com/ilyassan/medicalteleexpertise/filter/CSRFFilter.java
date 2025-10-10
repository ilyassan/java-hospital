package com.ilyassan.medicalteleexpertise.filter;

import com.ilyassan.medicalteleexpertise.util.CSRFUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CSRFFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {

            String requestURI = httpRequest.getRequestURI();
            if (requestURI != null && requestURI.endsWith("/login")) {
                chain.doFilter(request, response);
                return;
            }

            if (!CSRFUtil.validateToken(httpRequest)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
                return;
            }
        } else {
            // For GET requests, ensure a token exists in the session
            CSRFUtil.getToken(httpRequest);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
