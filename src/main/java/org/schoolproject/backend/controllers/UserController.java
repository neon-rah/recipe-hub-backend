package org.schoolproject.backend.controllers;

import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //  Création d'un utilisateur avec upload d'image
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<UserDTO> createUser(
            @RequestPart("user") UserDTO userDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        UserDTO createdUser = userService.createUser(userDTO, profileImage);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable UUID id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
