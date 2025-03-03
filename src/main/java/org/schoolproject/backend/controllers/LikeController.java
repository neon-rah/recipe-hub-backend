package org.schoolproject.backend.controllers;

import org.schoolproject.backend.config.JwtUtil;
import org.schoolproject.backend.entities.Like;
import org.schoolproject.backend.services.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;
    private final JwtUtil jwtUtil;

    public LikeController(LikeService likeService, JwtUtil jwtUtil) {
        this.likeService = likeService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<Like> toggleLike(@PathVariable int recipeId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        Like like = likeService.toggleLike(userId, recipeId);
        if (like == null) {
            return new ResponseEntity<>(null, HttpStatus.OK); // Unlike status 200
        } else {
            return new ResponseEntity<>(like, HttpStatus.CREATED); // Like status 201
        }
    }

    @DeleteMapping("/recipe/{recipeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable int recipeId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        likeService.deleteLike(userId, recipeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<Boolean> isLikedByUser(@PathVariable int recipeId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        boolean liked = likeService.isLikedByUser(userId, recipeId);
        return new ResponseEntity<>(liked, HttpStatus.OK);
    }

    @GetMapping("/recipe/{recipeId}/list")
    public ResponseEntity<List<Like>> getLikesByRecipe(@PathVariable int recipeId) {
        List<Like> likes = likeService.getLikesByRecipe(recipeId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Like>> getLikesByUser(HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        List<Like> likes = likeService.getLikesByUser(userId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/recipe/{recipeId}/count")
    public ResponseEntity<Integer> getLikeCountByRecipe(@PathVariable int recipeId) {
        int likeCount = likeService.getLikeCountByRecipe(recipeId);
        return new ResponseEntity<>(likeCount, HttpStatus.OK);
    }


}


/*
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
        Like like = likeService.toogleLike(userId, recipeId);
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
*/
