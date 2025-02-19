package org.schoolproject.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.schoolproject.backend.dto.NotificationDTO;
import org.schoolproject.backend.entities.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // Mappage entre l'entité Notification et le DTO NotificationDTO
    @Mappings({
            @Mapping(source = "sender.idUser", target = "senderId"),  // Utilisation de sender au lieu de user
            @Mapping(source = "sender.lastName", target = "senderLastName"),
            @Mapping(source = "sender.firstName", target = "senderFirstName"),
            @Mapping(source = "sender.email", target = "senderEmail"),
            @Mapping(source = "sender.profilePic", target = "senderProfilePic"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "message", target = "message"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "read", target = "read")  // Utilisation de la propriété isRead dans l'entité
    })
    NotificationDTO toDTO(Notification notification);

    // Mappage inverse du DTO vers l'entité
    @Mappings({
            @Mapping(source = "senderId", target = "sender.idUser"),  // Mappage vers l'objet sender
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "message", target = "message"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "read", target = "read")  // Utilisation de la propriété isRead dans l'entité
    })
    Notification toEntity(NotificationDTO notificationDTO);
}
