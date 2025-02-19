package org.schoolproject.backend.services;

import org.schoolproject.backend.entities.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeService {
    Recipe createRecipe(Recipe recipe, MultipartFile recipeImage);
    Optional<Recipe> findRecipeById(int recipeId);
    List<Recipe> findAllRecipes();
    List<Recipe> findRecipesByUserId(UUID userId);
    Recipe updateRecipe(int recipeId, Recipe updatedRecipe, MultipartFile newRecipeImage);
    void deleteRecipe(int recipeId);

    List<Recipe> searchRecipes(String title, String region, String ingredient, String category);
}
