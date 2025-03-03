package org.schoolproject.backend.services;

import org.schoolproject.backend.dto.RecipeDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeService {
    RecipeDTO createRecipe(RecipeDTO recipeDTO, MultipartFile recipeImage, UUID userId);
    RecipeDTO updateRecipe(int recipeId, RecipeDTO recipeDTO, MultipartFile recipeImage, UUID userId);
//    RecipeDTO createRecipe(RecipeDTO recipeDTO, MultipartFile recipeImage);
//    RecipeDTO updateRecipe(int recipeId, RecipeDTO updatedRecipeDTO, MultipartFile newRecipeImage);
    Optional<RecipeDTO> findRecipeById(int recipeId);
    List<RecipeDTO> findAllRecipes();
    List<RecipeDTO> findRecipesByUserId(UUID userId);
    void deleteRecipe(int recipeId);
    List<RecipeDTO> searchRecipes(String title, String ingredient, String category);
    List<RecipeDTO> findRecipesWithUserInfo(UUID userId);
    Page<RecipeDTO> findRecipesExcludingUser(UUID userId, int page, int size);
    Page<RecipeDTO> searchRecipesExcludingUser(UUID userId, String query, int page, int size);
}
