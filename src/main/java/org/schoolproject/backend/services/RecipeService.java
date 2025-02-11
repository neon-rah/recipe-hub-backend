package org.schoolproject.backend.services;

import org.schoolproject.backend.entities.Recipe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeService {
    Recipe createRecipe(Recipe recipe);
    Optional<Recipe> findRecipeById(int recipeId);
    List<Recipe> findAllRecipes();
    List<Recipe> findRecipesByUserId(UUID userId);
    Recipe updateRecipe(int recipeId, Recipe updatedRecipe);
    void deleteRecipe(int recipeId);

    List<Recipe> searchRecipes(String title, String region, String ingredient, String category);
}
