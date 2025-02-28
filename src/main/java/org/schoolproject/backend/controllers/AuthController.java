package org.schoolproject.backend.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Classe pour structurer les réponses d'erreur
     */
    record ErrorResponse(int status, String error, String message) {}

    /**
     * Inscription d'un utilisateur
     */

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> register(
            MultipartHttpServletRequest request,
            HttpServletResponse response) {
        logger.info("Received registration request");

        try {
            // Extraire les champs du FormData
            String lastName = request.getParameter("lastName");
            String firstName = request.getParameter("firstName");
            String address = request.getParameter("address");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            // confirmPassword est ignoré ici car validé côté frontend

            // Validation minimale
            if (lastName == null || firstName == null || address == null || email == null || password == null) {
                throw new IllegalArgumentException("Missing required fields: lastName, firstName, address, email, or password");
            }

            // Créer UserDTO manuellement
            UserDTO userDTO = new UserDTO();
            userDTO.setLastName(lastName);
            userDTO.setFirstName(firstName);
            userDTO.setAddress(address);
            userDTO.setEmail(email);
            userDTO.setPassword(password);

            // Extraire le fichier profilePic
            MultipartFile profilePic = request.getFile("profilePic");

            logger.info("Received registration request for email: {}", email);
            logger.debug("UserDTO: lastName={}, firstName={}, address={}, email={}, profilePic provided: {}",
                    lastName, firstName, address, email, profilePic != null);

            // Appeler le service d'authentification
            Map<String, Object> authResponse = authService.register(userDTO, profilePic, response);
            logger.info("Registration successful for email: {}", email);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Registration failed", e);
            throw e; // Propager l'exception pour gestion par @ExceptionHandler
        }
    }

    /**
     * Connexion d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> loginData,
            HttpServletResponse response) {
        logger.info("Received login request");

        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            

            logger.info("Login attempt for email: {}", email);
            logger.debug("Email: {}, Password length: {}", email, password.length());

            Map<String, Object> authResponse = authService.login(email, password, response);
            logger.info("Login successful for email: {}", email);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Login failed", e);
            throw e;
        }
    }

    // Méthode utilitaire pour récupérer le refreshToken depuis les cookies
    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        logger.debug("Récupération du refreshToken depuis les cookies");
        if (request.getCookies() != null) {
            logger.debug("Cookies trouvés dans la requête: {}", (Object) request.getCookies());
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    logger.debug("Cookie refreshToken trouvé avec la valeur: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
            logger.debug("Aucun cookie refreshToken trouvé dans les cookies disponibles");
        } else {
            logger.debug("Aucun cookie présent dans la requête");
        }
        return null;
    }

    /**
     * Rafraîchir l'Access Token en utilisant le Refresh Token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(HttpServletRequest request) {
        logger.info("Requête reçue pour rafraîchir l'accessToken");

        try {
            String refreshToken = getRefreshTokenFromCookies(request);
            if (refreshToken == null) {
                logger.warn("Aucun refreshToken trouvé dans les cookies");
                throw new RuntimeException("No refresh token provided");
            }
            logger.debug("refreshToken extrait: {}", refreshToken);

            if (!authService.isRefreshTokenValid(refreshToken)) {
                logger.warn("refreshToken invalide ou expiré: {}", refreshToken);
                throw new RuntimeException("Invalid or expired refresh token");
            }

            logger.debug("Validation du refreshToken réussie, génération d'un nouvel accessToken");
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            logger.info("Nouvel accessToken généré avec succès");

            Map<String, String> responseBody = Map.of("accessToken", newAccessToken);
            logger.debug("Réponse préparée: {}", responseBody);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            logger.error("Erreur lors du rafraîchissement de l'accessToken", e);
            throw e; // Propager l'exception pour la gestion par @ExceptionHandler
        }
    }

    /**
     * Vérifier la validité d'un refreshToken
     */
    @PostMapping("/verify-refresh-token")
    public ResponseEntity<Map<String, Boolean>> verifyRefreshToken(HttpServletRequest request) {
        logger.info("Requête reçue pour vérifier la validité du refreshToken");

        try {
            String refreshToken = getRefreshTokenFromCookies(request);
            if (refreshToken == null) {
                logger.warn("Aucun refreshToken trouvé dans les cookies");
                return ResponseEntity.badRequest().body(Map.of("valid", false));
            }
            logger.debug("refreshToken extrait: {}", refreshToken);

            boolean isValid = authService.isRefreshTokenValid(refreshToken);
            logger.debug("Résultat de la validation du refreshToken: {}", isValid);

            Map<String, Boolean> responseBody = Map.of("valid", isValid);
            logger.debug("Réponse préparée: {}", responseBody);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification du refreshToken", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("valid", false));
        }
    }

    /**
     * Déconnexion de l'utilisateur
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        logger.info("Received logout request");

        try {
            Map<String, String> logoutResponse = authService.logout(response);
            logger.info("Logout successful");
            return ResponseEntity.ok(logoutResponse);
        } catch (Exception e) {
            logger.error("Logout failed", e);
            throw e;
        }
    }

    // Gestion des exceptions spécifiques
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPartException(MissingServletRequestPartException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Missing required part: " + ex.getRequestPartName(),
                ex.getMessage()
        );
        logger.warn("Missing request part: {}", ex.getRequestPartName());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid request data",
                ex.getMessage()
        );
        logger.warn("Invalid request data: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                ex.getMessage()
        );
        logger.error("Unexpected error occurred", ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}