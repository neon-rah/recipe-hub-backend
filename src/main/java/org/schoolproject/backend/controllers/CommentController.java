package org.schoolproject.backend.controllers;

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

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO, @RequestHeader("User-Id") UUID userId) {
        CommentDTO createdComment = commentService.createComment(commentDTO, userId);
        return ResponseEntity.ok(createdComment);
    }

    @PostMapping("/{parentCommentId}/reply")
    public ResponseEntity<CommentDTO> createReply(@RequestBody CommentDTO commentDTO, @RequestHeader("User-Id") UUID userId,
                                                  @PathVariable int parentCommentId) {
        CommentDTO createdReply = commentService.createReply(commentDTO, userId, parentCommentId);
        return ResponseEntity.ok(createdReply);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int commentId, @RequestHeader("User-Id") UUID userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByRecipeId(@PathVariable int recipeId) {
        List<CommentDTO> comments = commentService.getCommentsByRecipeId(recipeId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable int commentId) {
        CommentDTO comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }
}