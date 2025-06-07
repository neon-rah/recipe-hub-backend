package org.schoolproject.backend.services;

import org.schoolproject.backend.dto.CommentDTO;
import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO, UUID userId);
    CommentDTO createReply(CommentDTO commentDTO, UUID userId, int parentCommentId);
    void deleteComment(int commentId, UUID userId);
    List<CommentDTO> getCommentsByRecipeId(int recipeId);
    CommentDTO getCommentById(int commentId);
}