package org.schoolproject.backend.services;

import org.schoolproject.backend.entities.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Notification createNotification(UUID userId, String title, String message);
    List<Notification> getUserNotifications(UUID userId);
    int getUnreadCount(UUID userId);
    void markAllAsRead(UUID userId);
    void deleteNotification(int notificationId);
    void deleteAllNotifications(UUID userId);
}
