package org.schoolproject.backend.controllers;

import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //  Création d'un utilisateur avec upload d'image
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<UserDTO> createUser(
            @RequestPart("user") UserDTO userDTO,
            @RequestPart(value = "profilePic", required = false) MultipartFile profileImage) {

        UserDTO createdUser = userService.createUser(userDTO, profileImage);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable UUID id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile") // Changement de "/profile" à "/user/profile"
    public ResponseEntity<?> getUserProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        logger.info("Requête reçue pour récupérer le profil utilisateur");

        if (token == null || !token.startsWith("Bearer ")) {
            logger.warn("En-tête Authorization manquant ou mal formé: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing or invalid Authorization header"));
        }

        try {
            String accessToken = token.substring(7);
            logger.debug("AccessToken extrait: {}", accessToken);
            UserDTO user = userService.getUserProfileFromToken(accessToken);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            logger.warn("Erreur spécifique lors de la récupération du profil: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la récupération du profil", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> findUserByEmail(@PathVariable String email) {
        return userService.findUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.existsUserByEmail(email));
    }

    // Mise à jour d'un utilisateur avec image de profil
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @RequestPart("user") UserDTO updatedUser,
            @RequestPart(value = "profileImage", required = false) MultipartFile newProfileImage) {

        UserDTO user = userService.updateUser(id, updatedUser, newProfileImage);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify-password")
    public ResponseEntity<Boolean> verifyPassword(@PathVariable UUID id, @RequestBody String password) {
        return ResponseEntity.ok(userService.verifyPassword(id, password));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable UUID id, @RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    //  Endpoint pour récupérer le profil utilisateur avec ses recettes
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable UUID id) {
        return userService.getUserProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
