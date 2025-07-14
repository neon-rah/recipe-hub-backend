package org.schoolproject.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CommentDTO {
    private int idComment;
    private UUID userId;
    private int recipeId;
    private String userFullName;
    private String userProfilePic;
    private int parentId;
    private String content;
    private LocalDateTime createdAt;
//    private List<CommentDTO> replies;
    private boolean deleted = false;
}
