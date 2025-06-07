package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.dto.CommentDTO;
import org.schoolproject.backend.entities.Comment;
import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.mappers.CommentMapper;
import org.schoolproject.backend.repositories.CommentRepository;
import org.schoolproject.backend.repositories.RecipeRepository;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentMapper commentMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationServiceImpl notificationService;
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository,
                              RecipeRepository recipeRepository, CommentMapper commentMapper,
                              SimpMessagingTemplate messagingTemplate, NotificationServiceImpl notificationService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.commentMapper = commentMapper;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Recipe recipe = recipeRepository.findById(commentDTO.getRecipeId())
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setUser(user);
        comment.setRecipe(recipe);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        CommentDTO savedCommentDTO = commentMapper.toDto(savedComment);

        // Broadcast comment to all subscribers of the recipe
        messagingTemplate.convertAndSend("/topic/comments/" + commentDTO.getRecipeId(), savedCommentDTO);

        // Notify recipe owner if commenter is not the owner
        if (!recipe.getUser().getIdUser().equals(userId)) {
            notificationService.sendCommentNotification(userId, recipe.getUser().getIdUser(), recipe.getIdRecipe(), commentDTO.getContent());
        }

        return savedCommentDTO;
    }

    @Override
    @Transactional
    public CommentDTO createReply(CommentDTO commentDTO, UUID userId, int parentCommentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
        Recipe recipe = recipeRepository.findById(parentComment.getRecipe().getIdRecipe())
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        Comment reply = commentMapper.toEntity(commentDTO);
        reply.setUser(user);
        reply.setRecipe(recipe);
        reply.setParent(parentComment);
        reply.setCreatedAt(LocalDateTime.now());

        Comment savedReply = commentRepository.save(reply);
        CommentDTO savedReplyDTO = commentMapper.toDto(savedReply);

        // Broadcast reply to all subscribers of the recipe
        messagingTemplate.convertAndSend("/topic/comments/" + recipe.getIdRecipe(), savedReplyDTO);

        // Notify parent comment owner if replier is not the owner
        if (!parentComment.getUser().getIdUser().equals(userId)) {
            notificationService.sendReplyNotification(userId, parentComment.getUser().getIdUser(), recipe.getIdRecipe(), commentDTO.getContent());
        }

        return savedReplyDTO;
    }

    @Override
    @Transactional
    public void deleteComment(int commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getUser().getIdUser().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this comment");
        }
        CommentDTO deletedCommentDTO = commentMapper.toDto(comment);
        deletedCommentDTO.setDeleted(true);
        commentRepository.delete(comment);

        // Notify subscribers of the deletion
        messagingTemplate.convertAndSend("/topic/comments/" + comment.getRecipe().getIdRecipe(),
                deletedCommentDTO);
    }

    @Override
    public List<CommentDTO> getCommentsByRecipeId(int recipeId) {
        List<Comment> comments = commentRepository.findAllByRecipeIdRecipe(recipeId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO getCommentById(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        return commentMapper.toDto(comment);
    }
}