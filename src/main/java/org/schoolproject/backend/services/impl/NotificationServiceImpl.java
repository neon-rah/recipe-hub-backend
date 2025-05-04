package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.dto.NotificationDTO;
import org.schoolproject.backend.entities.Notification;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.mappers.NotificationMapper;
import org.schoolproject.backend.repositories.FollowerRepository;
import org.schoolproject.backend.repositories.NotificationRepository;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper; // Utilisation du mapper
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);


    public NotificationServiceImpl(NotificationRepository notificationRepository, FollowerRepository followerRepository,
                                   UserRepository userRepository,
                                   NotificationMapper notificationMapper, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public NotificationDTO createNotification(UUID userId, String title, String message) {
        return userRepository.findById(userId)
                .map(user -> {
                    Notification notification = new Notification();
                    notification.setTitle(title);
                    notification.setMessage(message);
                    notification.setUser(user);

                    Notification savedNotification = notificationRepository.save(notification);
                    return notificationMapper.toDTO(savedNotification); // Conversion en DTO avant de retourner
                }).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<NotificationDTO> getUserNotifications(UUID userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdUserOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(notificationMapper::toDTO)  // Conversion en DTO
                .collect(Collectors.toList());
    }

    @Override
    public int getUnreadCount(UUID userId) {
        return notificationRepository.countAllByUserIdUserAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findAllByUserIdUserAndReadFalseOrderByCreatedAtDesc(userId);
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
        notificationRepository.deleteAllByUserIdUser(userId);
    }

    @Override
    public NotificationDTO getNotificationById(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        return notificationMapper.toDTO(notification);  // Retourne le DTO de la notification
    }

    /**
     * Envoie une notification lorsqu'un utilisateur suit un autre utilisateur.
     *
     * @param followerId ID de l'utilisateur qui suit
     * @param followedId ID de l'utilisateur suivi
     */
    @Override
    @Transactional
    public void sendFollowNotification(UUID followerId, UUID followedId) {
        Optional<User> followerOpt = userRepository.findById(followerId);
        Optional<User> followedOpt = userRepository.findById(followedId);

        if (followerOpt.isEmpty() || followedOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User follower = followerOpt.get();
        User followed = followedOpt.get();

        String title = "New follower";
        String message = ((follower.getFirstName() != null) ? follower.getFirstName() + " " + follower.getLastName() : follower.getLastName()) + " follow you.";

        Notification notification = Notification.builder()
                .user(followed)
                .sender(follower)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .read(false)
                .relatedEntityId(null)
                .entityType("user")
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // Envoi via WebSocket
        messagingTemplate.convertAndSend("/topic/notifications/" + followedId, notificationMapper.toDTO(savedNotification));
    }

    @Override
    @Transactional
    public void markAsRead(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }


    /**
     * Envoie une notification à tous les abonnés lorsqu'un utilisateur publie une recette.
     *
     * @param userId      ID de l'utilisateur qui publie
     * @param recipeId    ID de la recette publiée
     * @param recipeTitle Titre de la recette
     */


    @Override
    @Transactional
    public void sendRecipePublicationNotification(UUID userId, int recipeId, String recipeTitle) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.error("Utilisateur {} non trouvé", userId);
            throw new IllegalArgumentException("User not found");
        }

        User author = userOpt.get();
        logger.debug("Auteur : {}", author);

        // Utiliser la nouvelle méthode pour obtenir les abonnés
        List<User> followers = getFollowers(userId);
        logger.debug("Nombre d’abonnés pour {} : {}", userId, followers.size());
        followers.forEach(follower -> logger.debug("Abonné : {} - {}", follower.getIdUser(), follower.getEmail()));

        if (followers.isEmpty()) {
            logger.warn("Aucun abonné trouvé pour l’utilisateur {}", userId);
            return; // Sortir si aucun abonné
        }

        String title = "New recipe";
        String message = (author.getFirstName() != null ? author.getFirstName() + " " + author.getLastName() : author.getLastName()) + " published a new recipe : " + recipeTitle;

        for (User follower : followers) {
            Notification notification = Notification.builder()
                    .user(follower)
                    .sender(author)
                    .title(title)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .read(false)
                    .relatedEntityId(recipeId)
                    .entityType("recipe")
                    .build();

            Notification savedNotification = notificationRepository.save(notification);
            logger.debug("Notification enregistrée pour {} : {}", follower.getIdUser(), savedNotification);

            messagingTemplate.convertAndSend("/topic/notifications/" + follower.getIdUser(), notificationMapper.toDTO(savedNotification));
            logger.debug("Notification envoyée via WebSocket à /topic/notifications/{}", follower.getIdUser());
        }
    }

    @Override
    public void markAllAsSeen(UUID userId) {
        List<Notification> unseenNotification = notificationRepository.findAllByUserIdUserAndSeenFalseOrderByCreatedAtDesc(userId);

        unseenNotification.forEach(notification -> notification.setSeen(true));
        notificationRepository.saveAll(unseenNotification);
        logger.debug("Toutes les notifications marquées comme vues pour l’utilisateur {}", userId);
    }

    @Override
    public int getUnseenCount(UUID userId) {
        return notificationRepository.countAllByUserIdUserAndReadFalse(userId);
    }

    // Méthode modifiée pour retourner List<User>
    private List<User> getFollowers(UUID userId) {
        List<User> followers = followerRepository.findFollowersByFollowedId(userId);
        logger.debug("Nombre d’abonnés récupérés pour {} : {}", userId, followers.size());
        followers.forEach(follower -> logger.debug("Abonné : {} - {}", follower.getIdUser(), follower.getEmail()));
        return followers;
    }
}