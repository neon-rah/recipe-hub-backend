package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.mappers.RecipeMapper;
import org.schoolproject.backend.mappers.UserMapper;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.FileStorageService;
import org.schoolproject.backend.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;
    private final RecipeMapper recipeMapper;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO, MultipartFile profileImage) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        String imageUrl = (profileImage != null && !profileImage.isEmpty())
                ? fileStorageService.storeFile(profileImage, "user", null)
                : null;

        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setProfilePic(imageUrl);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public Optional<UserDTO> findUserById(UUID userId) {
        return userRepository.findById(userId).map(userMapper::toDto);
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDto);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserDTO updateUser(UUID userId, UserDTO updatedUserDTO, MultipartFile newProfileImage) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(updatedUserDTO.getFirstName());
            existingUser.setLastName(updatedUserDTO.getLastName());
            existingUser.setEmail(updatedUserDTO.getEmail());
            existingUser.setAddress(updatedUserDTO.getAddress());

            if (newProfileImage != null && !newProfileImage.isEmpty()) {
                String newImageUrl = fileStorageService.storeFile(newProfileImage, "user", existingUser.getProfilePic());
                existingUser.setProfilePic(newImageUrl);
            }

            return userMapper.toDto(userRepository.save(existingUser));
        }).orElseThrow(() -> new IllegalArgumentException("User not found"));
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

    @Override
    @Transactional
    public Optional<UserDTO> getUserProfile(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDtoWithRecipes);
    }

    private UserDTO toDtoWithRecipes(User user) {
        UserDTO userDTO = userMapper.toDto(user);
        userDTO.setRecipes(
                user.getRecipes() != null ?
                        user.getRecipes().stream().map(recipeMapper::toDto).toList() :
                        Collections.emptyList()
        );
        return userDTO;
    }
}