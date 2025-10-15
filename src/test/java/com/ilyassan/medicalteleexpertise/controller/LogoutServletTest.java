package com.ilyassan.medicalteleexpertise.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private LogoutServlet logoutServlet;

    @BeforeEach
    void setUp() {
        logoutServlet = new LogoutServlet();
    }

    @Test
    void testIndex_WhenUserIsLoggedIn_ShouldInvalidateSessionAndRedirect() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");

        logoutServlet.index(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/app/login");
    }

    @Test
    void testIndex_WhenNoSession_ShouldRedirectToLogin() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/app");

        logoutServlet.index(request, response);

        verify(response).sendRedirect("/app/login");
        verify(session, never()).invalidate();
    }

    @Test
    void testIndex_ShouldInvalidateSessionOnlyOnce() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");

        logoutServlet.index(request, response);

        verify(session, times(1)).invalidate();
    }

    @Test
    void testIndex_ShouldRedirectAfterInvalidation() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");

        logoutServlet.index(request, response);

        var inOrder = inOrder(session, response);
        inOrder.verify(session).invalidate();
        inOrder.verify(response).sendRedirect("/app/login");
    }

    @Test
    void testIndex_WithDifferentContextPath_ShouldRedirectCorrectly() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");

        logoutServlet.index(request, response);

        verify(response).sendRedirect("/app/login");
    }
}
