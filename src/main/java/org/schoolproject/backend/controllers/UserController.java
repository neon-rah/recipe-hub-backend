package org.schoolproject.backend.controllers;

import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<UserDTO> createUser(
            @RequestPart("user") UserDTO userDTO,
            @RequestPart(value = "profilePic", required = false) MultipartFile profileImage) {
        logger.info("Requête reçue pour créer un utilisateur");
        UserDTO createdUser = userService.createUser(userDTO, profileImage);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable UUID id) {
        logger.info("Requête reçue pour récupérer l'utilisateur avec ID: {}", id);
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        logger.info("Requête reçue pour récupérer le profil utilisateur");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String accessToken = token.substring(7);
        logger.debug("AccessToken extrait: {}", accessToken);
        UserDTO user = userService.getUserProfileFromToken(accessToken);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> findUserByEmail(@PathVariable String email) {
        logger.info("Requête reçue pour récupérer l'utilisateur avec email: {}", email);
        return userService.findUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        logger.info("Requête reçue pour récupérer tous les utilisateurs");
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email) {
        logger.info("Requête reçue pour vérifier l'existence de l'email: {}", email);
        return ResponseEntity.ok(userService.existsUserByEmail(email));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            MultipartHttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        logger.info("Requête reçue pour mettre à jour l'utilisateur avec ID: {}", id);

        if (token == null || !token.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        try {
            // Extraire les champs du FormData
            String lastName = request.getParameter("lastName");
            String firstName = request.getParameter("firstName");
            String address = request.getParameter("address");
            String email = request.getParameter("email");

            // Validation minimale
            if (lastName == null || firstName == null || address == null || email == null) {
                throw new IllegalArgumentException("Missing required fields: lastName, firstName, address, or email");
            }

            // Créer UserDTO
            UserDTO updatedUser = new UserDTO();
            updatedUser.setLastName(lastName);
            updatedUser.setFirstName(firstName);
            updatedUser.setAddress(address);
            updatedUser.setEmail(email);

            // Extraire l'image
            MultipartFile profileImage = request.getFile("profileImage");

            logger.info("Mise à jour de l'utilisateur avec ID: {}, email: {}, profileImage provided: {}", id, email, profileImage != null);

            String accessToken = token.substring(7);
            UserDTO updatedUserDTO = userService.updateUser(id, updatedUser, profileImage, accessToken);
            return ResponseEntity.ok(updatedUserDTO);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid user data", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        logger.info("Requête reçue pour supprimer l'utilisateur avec ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify-password")
    public ResponseEntity<Boolean> verifyPassword(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        logger.info("Requête reçue pour vérifier le mot de passe de l'utilisateur avec ID: {}", id);
        String password = request.get("password");
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        return ResponseEntity.ok(userService.verifyPassword(id, password));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        logger.info("Requête reçue pour changer le mot de passe de l'utilisateur avec ID: {}", id);

        if (token == null || !token.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        if (currentPassword == null || newPassword == null || currentPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Current password and new password are required");
        }

        String accessToken = token.substring(7);
        userService.changePassword(id, currentPassword, newPassword, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable UUID id) {
        logger.info("Requête reçue pour récupérer le profil utilisateur avec ID: {}", id);
        return userService.getUserProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}