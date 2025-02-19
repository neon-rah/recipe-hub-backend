package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.dto.NotificationDTO;
import org.schoolproject.backend.entities.Notification;
import org.schoolproject.backend.mappers.NotificationMapper;
import org.schoolproject.backend.repositories.NotificationRepository;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper; // Utilisation du mapper

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional
    public NotificationDTO createNotification(UUID userId, String title, String message) {
        return userRepository.findById(userId)
                .map(user -> {
                    Notification notification = new Notification();
                    notification.setTitle(title);
                    notification.setMessage(message);
                    notification.setUser(user);

                    Notification savedNotification = notificationRepository.save(notification);
                    return notificationMapper.toDTO(savedNotification); // Conversion en DTO avant de retourner
                }).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<NotificationDTO> getUserNotifications(UUID userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdUserOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(notificationMapper::toDTO)  // Conversion en DTO
                .collect(Collectors.toList());
    }

    @Override
    public int getUnreadCount(UUID userId) {
        return notificationRepository.countAllByUserIdUserAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findAllByUserIdUserAndReadFalseOrderByCreatedAtDesc(userId);
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
        });
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void deleteNotification(int notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(UUID userId) {
        notificationRepository.deleteAllByUserIdUser(userId);
    }

    @Override
    public NotificationDTO getNotificationById(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        return notificationMapper.toDTO(notification);  // Retourne le DTO de la notification
    }
}
