package org.schoolproject.backend.services;

import org.schoolproject.backend.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDTO createUser(UserDTO userDTO, MultipartFile profileImage);

    UserDTO getUserProfileFromToken(String token);

    Optional<UserDTO> findUserById(UUID userId);

    Optional<UserDTO> findUserByEmail(String email);

    List<UserDTO> findAllUsers();

    boolean existsUserByEmail(String email);

    UserDTO updateUser(UUID userId, UserDTO updatedUserDTO, MultipartFile newProfileImage, String token);

    void deleteUser(UUID userId);

    boolean verifyPassword(UUID id, String password);

    void changePassword(UUID id, String currentPassword, String newPassword, String token);

    Optional<UserDTO> getUserProfile(UUID userId);
}