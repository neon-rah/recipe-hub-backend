package org.schoolproject.backend.controllers;

import org.schoolproject.backend.entities.Notification;
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


    @PostMapping("/{userId}")
    public ResponseEntity<Notification> createNotification(@PathVariable UUID userId, @RequestParam String title, @RequestParam String message) {
        try {
            Notification notification = notificationService.createNotification(userId, title, message);
            return new ResponseEntity<>(notification, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable UUID userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }


    @GetMapping("/{userId}/unread")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable UUID userId) {
        int unreadCount = notificationService.getUnreadCount(userId);
        return new ResponseEntity<>(unreadCount, HttpStatus.OK);
    }


    @PutMapping("/{userId}/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable UUID userId) {
        notificationService.markAllAsRead(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("/{notifId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable int notifId) {
        notificationService.deleteNotification(notifId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable UUID userId) {
        notificationService.deleteAllNotifications(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
