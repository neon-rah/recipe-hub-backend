package org.schoolproject.backend.controllers;

import org.schoolproject.backend.entities.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequestMapping("/api")
public class TestController {
    private final SimpMessagingTemplate messagingTemplate;

    public TestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/test-notification/{userId}")
    public ResponseEntity<String> sendTestNotification(@PathVariable String userId) {
        String notification = "hello world notif";

        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
        System.out.println("Notification envoyée à /topic/notifications/" + userId + ": " + notification);
        return ResponseEntity.ok("Notification envoyée à " + userId);
    }
}
