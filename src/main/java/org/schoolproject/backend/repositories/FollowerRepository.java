package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Follower;
import org.schoolproject.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
   /* List<Follower> findAllByFollowerUserId(UUID followerId);
    List<Follower> findAllByFollowedUserId(UUID followedId);
    int countByFollowerUserId(UUID followerId);
    int countByFollowedUserId(UUID followedId);*/

}
