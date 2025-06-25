package org.schoolproject.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.schoolproject.backend.config.JwtUtil;
import org.schoolproject.backend.dto.CommentDTO;
import org.schoolproject.backend.services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    public CommentController(CommentService commentService, JwtUtil jwtUtil) {
        this.commentService = commentService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @RequestBody CommentDTO commentDTO,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);
        CommentDTO createdComment = commentService.createComment(commentDTO, userId);
        return ResponseEntity.ok(createdComment);
    }

    @PostMapping("/{parentId}/reply")
    public ResponseEntity<CommentDTO> createReply(
            @RequestBody CommentDTO commentDTO,
            @PathVariable int parentId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);
        CommentDTO createdReply = commentService.createReply(commentDTO, userId, parentId);
        return ResponseEntity.ok(createdReply);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable int id,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByRecipeId(@PathVariable int recipeId) {
        List<CommentDTO> comments = commentService.getCommentsByRecipeId(recipeId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable int id) {
        CommentDTO comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }
}