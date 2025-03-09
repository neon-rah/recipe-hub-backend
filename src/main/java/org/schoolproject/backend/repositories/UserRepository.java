package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
//    Optional<User> findByPhone(String phone);
    Optional<User> findByIdUser(UUID userId);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String query, String query2);
}
