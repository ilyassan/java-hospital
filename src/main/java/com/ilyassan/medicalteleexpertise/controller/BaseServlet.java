package com.ilyassan.medicalteleexpertise.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public abstract class BaseServlet extends HttpServlet {

    private static final String VIEWS_PATH = "/WEB-INF/views/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "index"; // Default action
        }

        try {
            Method method = this.getClass().getMethod(action, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, request, response);
        } catch (NoSuchMethodException e) {
            throw new ServletException("Action not found: " + action, e);
        } catch (Exception e) {
            throw new ServletException("Error executing action: " + action, e);
        }
    }

    /**
     * Helper method to render a view
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param viewName View name without path (e.g., "login.jsp")
     */
    protected void view(HttpServletRequest request, HttpServletResponse response, String viewName) throws ServletException, IOException {
        request.getRequestDispatcher(VIEWS_PATH + viewName).forward(request, response);
    }
}
