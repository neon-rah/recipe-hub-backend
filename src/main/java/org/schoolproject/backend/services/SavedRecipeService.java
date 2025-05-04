package org.schoolproject.backend.services;

import org.schoolproject.backend.dto.RecipeDTO;
import org.schoolproject.backend.entities.SavedRecipe;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SavedRecipeService {
    SavedRecipe toggleSavedRecipe(UUID userId, int recipeId);
    void removeSavedRecipe(UUID userId, int recipeId);
    List<SavedRecipe> getSavedRecipes(UUID userId);
    boolean isSavedRecipe(UUID userId, int recipeId);
    void clearAllSavedRecipes(UUID userId);

    Page<RecipeDTO> getSavedRecipesPaged(UUID userId, int page, int size, String category);

}
