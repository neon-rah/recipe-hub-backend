package org.schoolproject.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint pour l'inscription d'un utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user, HttpServletResponse response) {
        String accessToken = authService.register(user, response);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    /**
     * Endpoint pour la connexion d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        String accessToken = authService.login(email, password, response);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    /**
     * Endpoint pour rafraîchir l'Access Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request) {
        // Récupérer le Refresh Token depuis les cookies
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token not found"));
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(response));
    }
}
