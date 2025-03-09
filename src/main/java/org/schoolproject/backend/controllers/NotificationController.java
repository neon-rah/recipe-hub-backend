package org.schoolproject.backend.controllers;

import org.schoolproject.backend.dto.NotificationDTO;
import org.schoolproject.backend.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Créer une notification
    @PostMapping("/{userId}")
    public ResponseEntity<NotificationDTO> createNotification(
            @PathVariable UUID userId,
            @RequestParam String title,
            @RequestParam String message) {
        NotificationDTO notificationDTO = notificationService.createNotification(userId, title, message);
        return new ResponseEntity<>(notificationDTO, HttpStatus.CREATED);
    }

    // Récupérer toutes les notifications d'un utilisateur
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable UUID userId) {
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // Récupérer le nombre de notifications non lues
    @GetMapping("/{userId}/unread")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable UUID userId) {
        int unreadCount = notificationService.getUnreadCount(userId);
        return new ResponseEntity<>(unreadCount, HttpStatus.OK);
    }

    // Marquer toutes les notifications comme lues
    @PutMapping("/{userId}/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable UUID userId) {
        notificationService.markAllAsRead(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Supprimer une notification par son ID
    @DeleteMapping("/{notifId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable int notifId) {
        notificationService.deleteNotification(notifId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Supprimer toutes les notifications d'un utilisateur
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable UUID userId) {
        notificationService.deleteAllNotifications(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{notifId}/mark-read")
    public ResponseEntity<Void> markAsRead(@PathVariable int notifId) {
        notificationService.markAsRead(notifId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Récupérer une notification spécifique par son ID
    @GetMapping("/notification/{notifId}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable int notifId) {
        NotificationDTO notificationDTO = notificationService.getNotificationById(notifId);
        return notificationDTO != null ? new ResponseEntity<>(notificationDTO, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
