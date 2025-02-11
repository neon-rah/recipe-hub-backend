package org.schoolproject.backend.controllers;

import org.schoolproject.backend.services.FollowerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/followers")

public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }


    @PostMapping("/{followerId}/follow/{followedId}")
    public ResponseEntity<String> followUser(@PathVariable UUID followerId, @PathVariable UUID followedId) {
        followerService.followUser(followerId, followedId);
        return new ResponseEntity<>("User followed successfully", HttpStatus.CREATED);
    }


    @DeleteMapping("/{followerId}/unfollow/{followedId}")
    public ResponseEntity<String> unfollowUser(@PathVariable UUID followerId, @PathVariable UUID followedId) {
        followerService.unfollowUser(followerId, followedId);
        return new ResponseEntity<>("User unfollowed successfully", HttpStatus.OK);
    }

    // Vérifier si un utilisateur suit un autre
    @GetMapping("/{followerId}/is-following/{followedId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable UUID followerId, @PathVariable UUID followedId) {
        return ResponseEntity.ok(followerService.isFollowing(followerId, followedId));
    }

    // Obtenir la liste des abonnés
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<?>> getFollowers(@PathVariable UUID userId) {
        return ResponseEntity.ok(followerService.getFollowers(userId));
    }

    // Obtenir la liste des abonnements
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<?>> getFollowing(@PathVariable UUID userId) {
        return ResponseEntity.ok(followerService.getFollowing(userId));
    }

    // Obtenir le nombre d'abonnés
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Integer> getFollowerCount(@PathVariable UUID userId) {
        return ResponseEntity.ok(followerService.getFollowerCount(userId));
    }

    // Obtenir le nombre d'abonnements
    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Integer> getFollowingCount(@PathVariable UUID userId) {
        return ResponseEntity.ok(followerService.getFollowingCount(userId));
    }
}
