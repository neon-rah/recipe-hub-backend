package org.schoolproject.backend.services;

import org.schoolproject.backend.dto.RecipeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeService {
    RecipeDTO createRecipe(RecipeDTO recipeDTO, MultipartFile recipeImage);
    Optional<RecipeDTO> findRecipeById(int recipeId);
    List<RecipeDTO> findAllRecipes();
    List<RecipeDTO> findRecipesByUserId(UUID userId);
    RecipeDTO updateRecipe(int recipeId, RecipeDTO updatedRecipeDTO, MultipartFile newRecipeImage);
    void deleteRecipe(int recipeId);
    List<RecipeDTO> searchRecipes(String title, String region, String ingredient, String category);
    List<RecipeDTO> findRecipesWithUserInfo(UUID userId); 
}
