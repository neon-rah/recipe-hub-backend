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
import org.schoolproject.backend.repositories.VerificationCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    private final VerificationCodeService verificationCodeService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;

    @Value("${frontend.reset.link}")
    private String frontendResetLink;
    
    @Value("${reset.token.expiration.minutes}")
    private int expirationMinutes;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * envoye de code de verification d'email, confirmation avant inscription
     */
    public void initiateRegistration(String email){

        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email already in use.");
        }
        verificationCodeService.generateAndSendCode(email);
    }


    /**
     * Inscription d'un utilisateur
     */
    @Transactional
    public Map<String, Object> completeRegistration(UserDTO userDTO,String code, MultipartFile profileImage, HttpServletResponse response) {
        // Vérifier le code
        verificationCodeService.verifyCode(userDTO.getEmail(), code);

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

            // Supprimer le code de vérification
            verificationCodeRepository.deleteByEmail(userDTO.getEmail());

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
    private Map<String, Object> generateAuthResponse(User user, HttpServletResponse response) {
        logger.info("Génération de la réponse d'authentification pour: {}", user.getEmail());

        String accessToken = jwtUtil.generateAccessToken(user.getIdUser(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getIdUser(), user.getEmail());
        logger.debug("Tokens générés - accessToken: {}, refreshToken: {}", accessToken, refreshToken);

        // Supprimer l'ancien cookie refreshToken s'il existe
        Cookie clearCookie = new Cookie("refreshToken", null);
        clearCookie.setPath("/");
        clearCookie.setHttpOnly(true);
        clearCookie.setSecure(false); // Désactivé pour tests locaux
        clearCookie.setMaxAge(0); // Expire immédiatement
        response.addCookie(clearCookie);
        logger.debug("Ancien cookie refreshToken supprimé");

        // Ajouter le nouveau cookie refreshToken
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // Désactivé pour tests locaux
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setAttribute("SameSite", "Lax"); // ou "None" avec Secure=true en prod
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours
        response.addCookie(refreshTokenCookie);
        logger.debug("Nouveau cookie refreshToken ajouté: value={}, path={}, maxAge={}",
                refreshTokenCookie.getValue(), refreshTokenCookie.getPath(), refreshTokenCookie.getMaxAge());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("accessToken", accessToken);
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

    /**
     * Envoyer email pour la reinitialisation de mot de passe
     */
    @Transactional
    public Map<String, Object> initiatePasswordReset(String email) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        User user = userOptional.get();

        //generer un nouveau token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(expirationMinutes);

        //mise a jour de l'utilisateur dans la base de donnee
        user.setResetToken(resetToken);
        user.setResetTokenExpiredAt(expiryDate);
        userRepository.save(user);

        //lien de reinitialisation
        String resetLink = frontendResetLink+resetToken;

        // envoie de l'email
        try {
            String subject = "Reset password";
            String body = "Follow the link bellow to reset your password :\n"+resetLink+
                    "\n This link expires in "+expirationMinutes+ "minutes.";
            emailService.sendEmail(user.getEmail(), subject, body);

            response.put("success", true);
            response.put("message", "An Email has been sent. The reset link expires in "+expirationMinutes+"minutes.");
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de réinitialisation", e);
            throw new RuntimeException("An error occurred when sending email, please try again.", e);
        }

        return response;

    }

    /**
     * Vérifie la validité du token de réinitialisation
     */
    @Transactional
    public Map<String, Object> resetPassword(String token, String newPassword) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()){
            throw new IllegalArgumentException("Token invalid.");
        }
        User user = userOptional.get();

        //verification de l'expiration

        if(user.getResetTokenExpiredAt().isBefore(LocalDateTime.now())){
            user.setResetToken(null);
            user.setResetTokenExpiredAt(null);
            userRepository.save(user);
            throw new IllegalArgumentException("Expired reset password link");
        }

        //mette a jour sinon
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiredAt(null);
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Reset password successful");

        return response;

    }



}
