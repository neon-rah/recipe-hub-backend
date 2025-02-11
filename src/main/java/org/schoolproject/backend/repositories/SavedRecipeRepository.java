package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.entities.SavedRecipe;
import org.schoolproject.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Integer> {
    List<SavedRecipe> findAllByUserUserId(UUID userId);
    boolean existsByUserUserIdAndRecipeRecipeId(UUID userId, int recipeId);
    Optional<SavedRecipe> findByUserUserIdAndRecipeRecipeId(UUID userId, int recipeId);
    void deleteAllByUserUserId(UUID userId);
    void deleteByUserAndRecipe(User user, Recipe recipe);


}
