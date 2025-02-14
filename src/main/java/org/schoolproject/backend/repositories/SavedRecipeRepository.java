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
    List<SavedRecipe> findAllByUserIdUser(UUID userId);
    boolean existsByUserIdUserAndRecipeIdRecipe(UUID userId, int recipeId);
    Optional<SavedRecipe> findByUserIdUserAndRecipeIdRecipe(UUID userId, int recipeId);
    void deleteAllByUserIdUser(UUID userId);
    void deleteByUserAndRecipe(User user, Recipe recipe);


}
