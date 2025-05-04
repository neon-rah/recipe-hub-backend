package org.schoolproject.backend.services;

import org.schoolproject.backend.dto.NotificationDTO;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    NotificationDTO createNotification(UUID userId, String title, String message);
    List<NotificationDTO> getUserNotifications(UUID userId);
    int getUnreadCount(UUID userId);
    void markAllAsRead(UUID userId);
    void deleteNotification(int notificationId);
    void deleteAllNotifications(UUID userId);
    NotificationDTO getNotificationById(int notificationId);  // Nouvelle méthode pour obtenir une notification spécifique
     void sendFollowNotification(UUID followerId, UUID followedId);
    void markAsRead(int notificationId);
    void sendRecipePublicationNotification(UUID userId, int recipeId, String recipeTitle);

    void markAllAsSeen(UUID userId);
    int getUnseenCount(UUID userId);
    }
