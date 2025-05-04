package org.schoolproject.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private int idNotif;  // Identifiant de la notification
    private UUID idUser;
    private UUID senderId;  // ID de l'expéditeur (UUID)
    private String senderLastName;  // Nom de famille de l'expéditeur
    private String senderFirstName;  // Prénom de l'expéditeur
    private String senderEmail;  // Email de l'expéditeur
    private String senderProfilePic;  // Image de profil de l'expéditeur
    private String title;  // Titre de la notification
    private String message;  // Message de la notification
    private LocalDateTime createdAt;  // Date de création de la notification
    private boolean read;  // Statut de la notification (lue ou non)
    private boolean seen;
    private Integer relatedEntityId;  // ID de l'entité liée (recette) ou null si user
    private String entityType;  // Type d'entité (user ou recipe)
}
