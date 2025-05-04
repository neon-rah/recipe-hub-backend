package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.entities.Follower;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.repositories.FollowerRepository;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.FollowerService;
import org.schoolproject.backend.services.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class FollowerServiceImpl implements FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FollowerServiceImpl(FollowerRepository followerRepository, UserRepository userRepository, NotificationService notificationService) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public void followUser(UUID followerId, UUID followedId) {
        if(followerId.equals(followedId)) {
            throw new IllegalArgumentException("You can't follow yourself");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new IllegalArgumentException("Followed not found"));
        if(followerRepository.existsByFollowerAndFollowed(follower, followed)) {
            throw new IllegalArgumentException("Already following this User.");
        }
        Follower follow = new Follower();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        followerRepository.save(follow);
        notificationService.sendFollowNotification(followerId, followedId);

    }

    @Override
    @Transactional
    public void unfollowUser(UUID followerId, UUID followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));

        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new IllegalArgumentException("Followed user not found"));

        Follower follow = followerRepository.findByFollowerAndFollowed(follower, followed)
                .orElseThrow(() -> new IllegalArgumentException("Not following this user."));

        followerRepository.delete(follow);

    }

    @Override
    public boolean isFollowing(UUID followerId, UUID followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new IllegalArgumentException("Followed user not found"));

        return followerRepository.existsByFollowerAndFollowed(follower, followed);
    }

    @Override
    public List<Follower> getFollowers(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return followerRepository.findAllByFollowed(user);
    }

    @Override
    public List<Follower> getFollowing(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return followerRepository.findAllByFollower(user);
    }

    @Override
    public int getFollowerCount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return followerRepository.countByFollowed(user);
    }

    @Override
    public int getFollowingCount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return followerRepository.countByFollower(user);
    }

    // Nouvelle méthode pour suggestions (utilisateurs non suivis)
    @Override
    public List<User> getSuggestedUsers(UUID userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<User> allUsers = userRepository.findAll(Sort.by(Sort.Direction.ASC, "lastName", "firstName"));
        List<UUID> followingIds = followerRepository.findAllByFollower(currentUser)
                .stream()
                .map(follower -> follower.getFollowed().getIdUser())
                .collect(Collectors.toList());
        return allUsers.stream()
                .filter(user -> !user.getIdUser().equals(userId)) // Exclure l'utilisateur lui-même
                .filter(user -> !followingIds.contains(user.getIdUser())) // Exclure les utilisateurs suivis
                .collect(Collectors.toList());
    }

    // Nouvelle méthode pour recherche par nom/prénom
    @Override
    public List<User> searchUsers(UUID excludeUserId, String query) {
        return userRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(query, query)
                .stream()
                .filter(user -> !user.getIdUser().equals(excludeUserId))
                .sorted((u1, u2) -> {
                    int lastNameCompare = u1.getLastName().compareToIgnoreCase(u2.getLastName());
                    return lastNameCompare != 0 ? lastNameCompare : u1.getFirstName().compareToIgnoreCase(u2.getFirstName());
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<User> getRandomSuggestedUsers(UUID userId, int limit) {
        List<User> suggestedUsers = getSuggestedUsers(userId);
        Collections.shuffle(suggestedUsers, new Random());
        return suggestedUsers.stream().limit(limit).collect(Collectors.toList());
    }


}
