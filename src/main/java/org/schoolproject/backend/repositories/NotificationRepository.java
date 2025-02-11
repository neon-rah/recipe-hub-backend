package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserUserId(UUID userId);
    List<Notification> findAllByUserUserIdOrderByCreatedAtDesc(UUID userId);
    List<Notification> findAllByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId);
    int countAllByUserUserIdAndIsReadFalse(UUID userId);
    void deleteAllByUserUserId(UUID userId);

}
