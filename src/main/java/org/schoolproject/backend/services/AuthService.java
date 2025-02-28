package org.schoolproject.backend.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.schoolproject.backend.controllers.AuthController;
import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.mappers.UserMapper;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.config.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
    private final FileStorageService fileStorageService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    /**
     * Inscription d'un utilisateur
     */
    public Map<String, Object> register(UserDTO userDTO, MultipartFile profileImage, HttpServletResponse response) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        String imageUrl = null;
        try {
            // Stocker l'image si elle est fournie
            if (profileImage != null && !profileImage.isEmpty()) {
                imageUrl = fileStorageService.storeFile(profileImage, "user", null);
            }

            // Convertir UserDTO en entité User
            User user = userMapper.toEntity(userDTO);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setProfilePic(imageUrl);

            // Sauvegarder l'utilisateur dans la base de données
            userRepository.save(user);

            // Générer et retourner la réponse d'authentification
            return generateAuthResponse(user, response);

        } catch (Exception e) {
            // Gérer les erreurs spécifiques ou générales
            if (e instanceof IllegalArgumentException) {
                // Si l'erreur vient de fileStorageService ou userMapper avec un message spécifique
                throw new IllegalArgumentException("Invalid data provided: " + e.getMessage(), e);
            } else if (e instanceof DataIntegrityViolationException) {
                // Erreur de contrainte d'intégrité dans la base de données (autre que l'email, déjà vérifié)
                throw new IllegalStateException("Database constraint violation occurred during registration", e);
            } else if (e instanceof IOException) {
                // Erreur liée au stockage de fichier
                throw new RuntimeException("Failed to store profile image: " + e.getMessage(), e);
            } else {
                // Erreur générique (inclut des problèmes de DB ou autres exceptions inattendues)
                throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
            }
        }
    }


    /**
     * Connexion d'un utilisateur
     */
    public Map<String, Object> login(String email, String password, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return generateAuthResponse(user, response);
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

    public boolean isRefreshTokenValid(String refreshToken) {
        try {
            return jwtUtil.validateToken(refreshToken);
        } catch (Exception e) {
            return false; // Si une exception est levée (token invalide ou expiré), renvoyer false
        }
    }

    /**
     * Générer la réponse contenant UserDTO et Access Token
     */
//    private Map<String, Object> generateAuthResponse(User user, HttpServletResponse response) {
//        logger.info("Génération de la réponse d'authentification pour: {}", user.getEmail());
//
//        String accessToken = jwtUtil.generateAccessToken(user.getIdUser(), user.getEmail());
//        String refreshToken = jwtUtil.generateRefreshToken(user.getIdUser(), user.getEmail());
//        logger.debug("Tokens générés - accessToken: {}, refreshToken: {}", accessToken, refreshToken);
//
//        // Supprimer l'ancien cookie refreshToken s'il existe
//        Cookie clearCookie = new Cookie("refreshToken", null);
//        clearCookie.setPath("/");
//        clearCookie.setHttpOnly(true);
//        clearCookie.setSecure(false); // Désactivé pour tests locaux
//        clearCookie.setMaxAge(0); // Expire immédiatement
//        response.addCookie(clearCookie);
//        logger.debug("Ancien cookie refreshToken supprimé");
//
//        // Ajouter le nouveau cookie refreshToken
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(false); // Désactivé pour tests locaux
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setAttribute("SameSite", "Lax"); // ou "None" avec Secure=true en prod
//        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours
//        response.addCookie(refreshTokenCookie);
//        logger.debug("Nouveau cookie refreshToken ajouté: value={}, path={}, maxAge={}",
//                refreshTokenCookie.getValue(), refreshTokenCookie.getPath(), refreshTokenCookie.getMaxAge());
//
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("accessToken", accessToken);
//        responseBody.put("user", userMapper.toDto(user));
//        logger.debug("Réponse préparée: {}", responseBody);
//
//        return responseBody;
//    }



    /**
     * Générer la réponse contenant UserDTO, Access Token et Refresh Token
     */
    private Map<String, Object> generateAuthResponse(User user, HttpServletResponse response) {
        logger.info("Génération de la réponse d'authentification pour: {}", user.getEmail());

        String accessToken = jwtUtil.generateAccessToken(user.getIdUser(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getIdUser(), user.getEmail());
        logger.debug("Tokens générés - accessToken: {}, refreshToken: {}", accessToken, refreshToken);

        // Préparer la réponse avec refreshToken dans le corps
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("accessToken", accessToken);
        responseBody.put("refreshToken", refreshToken); // Ajouté au corps
        responseBody.put("user", userMapper.toDto(user));
        logger.debug("Réponse préparée: {}", responseBody);

        return responseBody;
    }
    public Map<String, String> logout(HttpServletResponse response) {
        // Supprimer le cookie du Refresh Token
        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // Expire immédiatement
        response.addCookie(refreshTokenCookie);

        return Map.of("message", "Déconnexion réussie");
    }
}
