package com.ilyassan.medicalteleexpertise.controller;

import com.ilyassan.medicalteleexpertise.enums.Role;
import com.ilyassan.medicalteleexpertise.model.User;
import com.ilyassan.medicalteleexpertise.util.PasswordUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private LoginServlet loginServlet;

    @BeforeEach
    void setUp() {
        loginServlet = new LoginServlet();
    }

    @Test
    void testIndex_WhenUserAlreadyLoggedIn_ShouldRedirectToDashboard() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(request.getContextPath()).thenReturn("/app");

        loginServlet.index(request, response);

        verify(response).sendRedirect("/app/dashboard");
    }

    @Test
    void testIndex_WhenUserNotLoggedIn_ShouldShowLoginPage() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        loginServlet.index(request, response);

        verify(request).getRequestDispatcher(contains("login.jsp"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAuthenticate_WhenEmailIsEmpty_ShouldShowError() throws ServletException, IOException {
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        loginServlet.authenticate(request, response);

        verify(request).setAttribute("error", "Email and password are required");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAuthenticate_WhenPasswordIsEmpty_ShouldShowError() throws ServletException, IOException {
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        loginServlet.authenticate(request, response);

        verify(request).setAttribute("error", "Email and password are required");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAuthenticate_WhenUserDoesNotExist_ShouldShowError() throws ServletException, IOException {
        when(request.getParameter("email")).thenReturn("nonexistent@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        try (MockedStatic<User> userMock = mockStatic(User.class)) {
            userMock.when(User::all).thenReturn(Arrays.asList());

            loginServlet.authenticate(request, response);

            verify(request).setAttribute("error", "Invalid email or password");
            verify(dispatcher).forward(request, response);
        }
    }

    @Test
    void testAuthenticate_WhenPasswordIsWrong_ShouldShowError() throws ServletException, IOException {
        when(request.getParameter("email")).thenReturn("nurse@example.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("nurse@example.com");
        testUser.setPassword(PasswordUtil.hashPassword("correctpassword"));
        testUser.setRole(Role.NURSE);

        try (MockedStatic<User> userMock = mockStatic(User.class)) {
            userMock.when(User::all).thenReturn(Arrays.asList(testUser));

            loginServlet.authenticate(request, response);

            verify(request).setAttribute("error", "Invalid email or password");
            verify(dispatcher).forward(request, response);
        }
    }

    @Test
    void testAuthenticate_WhenCredentialsAreCorrect_ShouldLoginSuccessfully() throws ServletException, IOException {
        String correctPassword = "password123";
        when(request.getParameter("email")).thenReturn("nurse@example.com");
        when(request.getParameter("password")).thenReturn(correctPassword);
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");

        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("nurse@example.com");
        testUser.setPassword(PasswordUtil.hashPassword(correctPassword));
        testUser.setRole(Role.NURSE);

        try (MockedStatic<User> userMock = mockStatic(User.class)) {
            userMock.when(User::all).thenReturn(Arrays.asList(testUser));

            loginServlet.authenticate(request, response);

            verify(session).setAttribute("userId", 1L);
            verify(session).setAttribute("userRole", "NURSE");
            verify(response).sendRedirect("/app/dashboard");
        }
    }
}
