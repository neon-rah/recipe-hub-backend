package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserIdUser(UUID userId);
    List<Notification> findAllByUserIdUserOrderByCreatedAtDesc(UUID userId);
    List<Notification> findAllByUserIdUserAndIsReadFalseOrderByCreatedAtDesc(UUID userId);
    int countAllByUserIdUserAndIsReadFalse(UUID userId);
    void deleteAllByUserIdUser(UUID userId);

}
