package org.schoolproject.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notif", updatable = false, nullable = false)
    private int idNotif;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", columnDefinition = "false")
    private boolean read = false;

    @Column(name = "is_seen", columnDefinition = "false")
    private boolean seen = false;

    @Column(name = "related_entity_id")
    private Integer relatedEntityId;  // Par exemple, ID de la recette, null si entity-type user

    @Column(name = "entity_type", length = 50)
    private String entityType;  // 'user' ou 'recipe' ou 'comment'
}
