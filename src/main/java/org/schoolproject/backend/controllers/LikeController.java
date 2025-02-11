package org.schoolproject.backend.controllers;

import org.schoolproject.backend.entities.Like;
import org.schoolproject.backend.services.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/likes")

public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }
    @PostMapping("/{userId}/recipe/{recipeId}")
    public ResponseEntity<Like> toggleLike(@PathVariable UUID userId, @PathVariable int recipeId) {
        Like like = likeService.createLike(userId, recipeId);
        if (like == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);  // C'est un unlike status code 200
        } else {
            return new ResponseEntity<>(like, HttpStatus.CREATED);  // C'est un like status code 201
        }
    }

    @DeleteMapping("/{userId}/recipe/{recipeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable UUID userId, @PathVariable int recipeId) {
        likeService.deleteLike(userId, recipeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{userId}/recipe/{recipeId}")
    public ResponseEntity<Boolean> isLikedByUser(@PathVariable UUID userId, @PathVariable int recipeId) {
        boolean liked = likeService.isLikedByUser(userId, recipeId);
        return new ResponseEntity<>(liked, HttpStatus.OK);
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<Like>> getLikesByRecipe(@PathVariable int recipeId) {
        List<Like> likes = likeService.getLikesByRecipe(recipeId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Like>> getLikesByUser(@PathVariable UUID userId) {
        List<Like> likes = likeService.getLikesByUser(userId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/recipe/{recipeId}/count")
    public ResponseEntity<Integer> getLikeCountByRecipe(@PathVariable int recipeId) {
        int likeCount = likeService.getLikeCountByRecipe(recipeId);
        return new ResponseEntity<>(likeCount, HttpStatus.OK);
    }

}
