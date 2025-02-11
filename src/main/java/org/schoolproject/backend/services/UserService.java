package org.schoolproject.backend.services;

import org.schoolproject.backend.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(User user);
    Optional<User> findUserById(UUID userId);
    Optional<User> findUserByEmail(String email);
    List<User> findAllUsers();
    boolean existsUserByEmail(String email);
    User updateUser(UUID userId, User updatedUser);
    void deleteUser(UUID userId);

    boolean verifyPassword(UUID id, String password);
    void changePassword(UUID id, String newPassword);
}
