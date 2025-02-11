package org.schoolproject.backend.services;

import org.schoolproject.backend.entities.SavedRecipe;

import java.util.List;
import java.util.UUID;

public interface SavedRecipeService {
    SavedRecipe addSavedRecipe(UUID userId, int recipeId);
    void removeSavedRecipe(UUID userId, int recipeId);
    List<SavedRecipe> getSavedRecipes(UUID userId);
    boolean isSavedRecipe(UUID userId, int recipeId);
    void clearAllSavedRecipes(UUID userId);

}
