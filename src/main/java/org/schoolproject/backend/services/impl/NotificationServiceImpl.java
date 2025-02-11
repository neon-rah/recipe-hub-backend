package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.entities.Notification;
import org.schoolproject.backend.repositories.NotificationRepository;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Notification createNotification(UUID userId, String title, String message) {
        return userRepository.findById(userId)
                .map(user -> {
                    Notification notification = new Notification();
                    notification.setTitle(title);
                    notification.setMessage(message);
                    notification.setUser(user);

                    return notificationRepository.save(notification);
                }).orElseThrow(()-> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findAllByUserUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public int getUnreadCount(UUID userId) {
        return notificationRepository.countAllByUserUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findAllByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
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
        notificationRepository.deleteAllByUserUserId(userId);
    }
}
