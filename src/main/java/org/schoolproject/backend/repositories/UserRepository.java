package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
//    Optional<User> findByPhone(String phone);
    Optional<User> findByUserId(UUID userId);
    boolean existsByEmail(String email);
}
