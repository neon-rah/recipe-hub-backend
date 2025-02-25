package org.schoolproject.backend.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.mappers.UserMapper;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.config.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Inscription d'un utilisateur
     */
    public String register(User user, HttpServletResponse response) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

       return generatedToken(user, response);
    }

    /**
     * Connexion d'un utilisateur
     */
    public String login(String email, String password, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return generatedToken(user, response);
    }

    /**
     * Rafraîchir l'Access Token en utilisant le Refresh Token
     */
    public String refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired token");
        }

        UUID userId = jwtUtil.extractUserId(refreshToken);
        String email = jwtUtil.extractEmail(refreshToken);
        return jwtUtil.generateAccessToken(userId, email);
    }

    private String generatedToken(User user, HttpServletResponse response) {
        String accessToken = jwtUtil.generateAccessToken(user.getIdUser(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getIdUser(), user.getEmail());

        // Stockage du Refresh Token en cookie sécurisé
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);

        return accessToken;
    }

    public Map<String, String> logout(HttpServletResponse response) {
        // Supprimer le cookie du Refresh Token
        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // Expire immédiatement
        response.addCookie(refreshTokenCookie);

        return Map.of("message", "Déconnexion réussie");
    }

}
