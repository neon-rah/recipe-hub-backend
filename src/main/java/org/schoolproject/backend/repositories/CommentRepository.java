package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByRecipeIdRecipe(int recipeId);
}
