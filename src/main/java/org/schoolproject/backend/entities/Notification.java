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
    private User user;  // Utilisateur destinataire de la notification (celui qui reçoit la notification)

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // Utilisateur qui envoie la notification (celui qui a effectué l'action, ex. suivi ou nouvelle recette)

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "related_entity_id")
    private Integer relatedEntityId;  // Par exemple, ID du profil ou de la recette

    @Column(name = "entity_type", length = 50)
    private String entityType;  // 'user' ou 'recipe'
}
