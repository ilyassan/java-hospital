package com.ilyassan.medicalteleexpertise.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("")
public class HomeServlet extends BaseServlet {

    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        view(request, response, "home.jsp");
    }
}
