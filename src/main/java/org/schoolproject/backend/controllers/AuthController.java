package org.schoolproject.backend.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.services.AuthService;
import org.schoolproject.backend.services.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    /**
     * Lance l'inscription en envoyant un code de vérification
     */
    @PostMapping(value = "/register")
    public ResponseEntity<Map<String, String>> initiateRegistration(
            @RequestBody Map<String, String> request) {
        logger.info("Received registration initiation request");

        try {
            // Extraire l'email
            String email = request.get("email");

            // Validation minimale
            if (email == null) {
                throw new IllegalArgumentException("Email is required");
            }

            logger.info("Initiating registration for email: {}", email);

            // Lancer le processus d'inscription
            authService.initiateRegistration(email);
            return ResponseEntity.ok(Map.of("message", "Code de vérification envoyé à votre email."));
        } catch (Exception e) {
            logger.error("Registration initiation failed", e);
            throw e;
        }
    }

    /**
     * Finalise l'inscription après vérification du code
     */
    @PostMapping(value = "/complete-registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            String code = request.getParameter("code");

            // Validation minimale
            if (lastName == null || firstName == null || address == null || email == null || password == null || code == null) {
                throw new IllegalArgumentException("Missing required fields: lastName, firstName, address, email, password or code");
            }

            // Créer UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setLastName(lastName);
            userDTO.setFirstName(firstName);
            userDTO.setAddress(address);
            userDTO.setEmail(email);
            userDTO.setPassword(password);

            // Extraire le fichier profilePic
            MultipartFile profilePic = request.getFile("profilePic");

            logger.info("Received registration request for email: {}", email);
            logger.debug("UserDTO: lastName={}, firstName={}, address={}, email={}, code={}, profilePic provided: {}",
                    lastName, firstName, address, email,code, profilePic != null);

            // finalisation
            Map<String, Object> authResponse = authService.completeRegistration(userDTO,code,profilePic, response);
            logger.info("Registration successful for email: {}", email);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Registration failed", e);
            throw e; // Propager l'exception pour gestion par @ExceptionHandler
        }
    }

    /**
     * Renvoyer un code de vérification
     */
    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(@RequestBody Map<String, String> request) {
        logger.info("Received resend code request");

        try {
            String email = request.get("email");

            if(email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            logger.info("Resend code request for email: {}", email);
            verificationCodeService.resendCode(email);
            return ResponseEntity.ok(Map.of("message", "Nouveau code de vérification envoyé."));
        } catch (Exception e) {
            logger.error("Resend code failed", e);
            throw e;
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
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            logger.debug("Cookies trouvés dans la requête: {}", (Object) request.getCookies());
            for (Cookie cookie : cookies) {
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
     * Vérifier la validité du refreshToken depuis les cookies
     */
    @PostMapping("/verify-refresh-token")
    public ResponseEntity<Map<String, Boolean>> verifyRefreshToken(
            @RequestBody(required = false) Map<String, String> requestBody,
            HttpServletRequest request) {
        logger.info("Requête reçue pour vérifier la validité du refreshToken");

        String refreshToken = null;

        // Étape 1 : Vérifier le corps de la requête
        if (requestBody != null && requestBody.containsKey("refreshToken")) {
            refreshToken = requestBody.get("refreshToken");
            if (refreshToken != null && !refreshToken.trim().isEmpty()) {
                logger.debug("refreshToken extrait du corps: {}", refreshToken);
            } else {
                logger.debug("refreshToken dans le corps est vide ou null");
            }
        } else {
            logger.debug("Aucun corps ou clé 'refreshToken' dans la requête");
        }

        // Étape 2 : Si absent ou vide dans le corps, tenter les cookies
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            refreshToken = getRefreshTokenFromCookies(request);
            if (refreshToken != null) {
                logger.debug("refreshToken extrait des cookies: {}", refreshToken);
            } else {
                logger.warn("Aucun refreshToken trouvé ni dans le corps ni dans les cookies");
                return ResponseEntity.badRequest().body(Map.of("valid", false));
            }
        }

        try {
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
     * Rafraîchir le token en lisant le refreshToken depuis les cookies
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request) {
        logger.info("Requête reçue pour rafraîchir le token");

        try {
            String refreshToken = getRefreshTokenFromCookies(request);
            if (refreshToken == null) {
                logger.warn("Aucun refreshToken trouvé dans les cookies");
                return ResponseEntity.badRequest().body(Map.of("error", "No refresh token provided"));
            }
            logger.debug("refreshToken extrait des cookies: {}", refreshToken);

            String newAccessToken = authService.refreshAccessToken(refreshToken);
            logger.debug("Nouveau accessToken généré: {}", newAccessToken);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", newAccessToken);
            logger.debug("Réponse préparée: {}", responseBody);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("Erreur lors du rafraîchissement du token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token refresh failed"));
        }
    }

    /**
     * Initie le processus de réinitialisation du mot de passe
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> initiateResetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email required");
        }

        Map<String, Object> responseBody = authService.initiatePasswordReset(email);

        return ResponseEntity.ok(responseBody);
    }

    /**
     * Réinitialise le mot de passe avec un token valide
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> requestBody) {

        String token = requestBody.get("token");
        String newPassword = requestBody.get("newPassword");

        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Token and new password required");
        }
        Map<String, Object> responseBody = authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(responseBody);
    }

}