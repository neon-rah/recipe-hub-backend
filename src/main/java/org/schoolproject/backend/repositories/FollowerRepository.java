package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Follower;
import org.schoolproject.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    boolean existsByFollowerAndFollowed(User follower, User followed);
//    boolean existsByFollowerIdUserAndFollowedIdUser(UUID followerId, UUID followedId);

    Optional<Follower> findByFollowerAndFollowed(User follower, User followed);
    List<Follower> findAllByFollower(User follower);
    List<Follower> findAllByFollowed(User followed);
    int countByFollower(User follower);
    int countByFollowed(User followed);

    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String query, String query2);
    Optional<Follower> findByFollowerIdUserAndFollowedIdUser(UUID followerId, UUID followedId);
    List<Follower> findAllByFollowedIdUser(UUID userId);
    List<Follower> findAllByFollowerIdUser(UUID userId);
    boolean existsByFollowerIdUserAndFollowedIdUser(UUID followerId, UUID followedId);
    int countByFollowedIdUser(UUID userId);
    int countByFollowerIdUser(UUID userId);
    // Nouvelle méthode pour récupérer les utilisateurs abonnés
    @Query("SELECT f.follower FROM Follower f WHERE f.followed.idUser = :followedId")
    List<User> findFollowersByFollowedId(UUID followedId);

}
