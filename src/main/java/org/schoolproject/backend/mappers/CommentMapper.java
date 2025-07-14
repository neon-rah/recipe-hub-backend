package org.schoolproject.backend.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.schoolproject.backend.dto.CommentDTO;
import org.schoolproject.backend.entities.Comment;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CommentMapper {

//    @Named("mapParentId")
//    default Integer mapParentId(final Comment comment) {
//        return comment != null ? comment.getIdComment() : null;
//    }

    @Mapping(target = "userId", source = "user.idUser")
    @Mapping(target = "recipeId", source = "recipe.idRecipe")
    @Mapping(target = "userFullName", expression = "java(" +
            "(comment.getUser().getFirstName() != null ? comment.getUser().getFirstName() : \"\") + " +
            "\" \" + " +
            "(comment.getUser().getLastName() != null ? comment.getUser().getLastName() : \"\")" +
            ")")
    @Mapping(target = "userProfilePic", source = "user.profilePic")
    @Mapping(target = "parentId", source = "parent.idComment")
//    @Mapping(target = "replies", source = "replies")
    CommentDTO toDto(Comment comment);

    List<CommentDTO> toDtoList(List<Comment> comments);


    @Mapping(target = "idComment", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "replies", ignore = true)
    Comment toEntity(CommentDTO commentDTO);
}
