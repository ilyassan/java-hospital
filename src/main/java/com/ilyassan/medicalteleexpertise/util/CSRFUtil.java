package com.ilyassan.medicalteleexpertise.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public class CSRFUtil {

    private static final String CSRF_TOKEN_SESSION_ATTR = "CSRF_TOKEN";
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);

        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        // Store in session
        session.setAttribute(CSRF_TOKEN_SESSION_ATTR, token);

        return token;
    }
    
    public static String getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);

        if (token == null || token.isEmpty()) {
            token = generateToken(request);
        }

        return token;
    }

    public static boolean validateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        String requestToken = request.getParameter("csrf_token");

        if (sessionToken == null || requestToken == null) {
            return false;
        }

        return constantTimeEquals(sessionToken, requestToken);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }

        return result == 0;
    }
}
