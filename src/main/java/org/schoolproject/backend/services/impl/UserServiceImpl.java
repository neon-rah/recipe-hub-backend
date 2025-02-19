package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.FileStorageService;
import org.schoolproject.backend.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final FileStorageService fileStorageService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public User createUser(User user, MultipartFile profileImage) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        String imageUrl = null;
        if(profileImage != null && !profileImage.isEmpty()) {
            imageUrl = fileStorageService.storeFile(profileImage, "user", null);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProfilePic(imageUrl);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, User updatedUser, MultipartFile newProfileImage) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
//            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setAddress(updatedUser.getAddress());

            if (newProfileImage != null && !newProfileImage.isEmpty()) {
                String newImageUrl = fileStorageService.storeFile(newProfileImage, "user", existingUser.getProfilePic());
                existingUser.setProfilePic(newImageUrl);
            }

            return userRepository.save(existingUser);
        }).orElseThrow(()-> new IllegalArgumentException("User not found"));
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        userRepository.findById(userId).ifPresentOrElse(user -> {
            if (user.getProfilePic() != null) {
                fileStorageService.deleteFile(user.getProfilePic());
            }
            userRepository.deleteById(userId);
        }, () -> {
            throw new IllegalArgumentException("User not found");
        });
    }

    @Override
    public boolean verifyPassword(UUID id, String password) {
        return userRepository.findById(id)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void changePassword(UUID id, String newPassword) {
        userRepository.findById(id).ifPresent(existingUser -> {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(existingUser);
        });
    }
}
