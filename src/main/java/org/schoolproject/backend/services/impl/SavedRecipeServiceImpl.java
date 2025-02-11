package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.entities.SavedRecipe;
import org.schoolproject.backend.repositories.RecipeRepository;
import org.schoolproject.backend.repositories.SavedRecipeRepository;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.SavedRecipeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service

public class SavedRecipeServiceImpl implements SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public SavedRecipeServiceImpl(SavedRecipeRepository savedRecipeRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.savedRecipeRepository = savedRecipeRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    @Transactional
    public SavedRecipe addSavedRecipe(UUID userId, int recipeId) {

        if(isSavedRecipe(userId, recipeId)) {
            throw new IllegalArgumentException("SavedRecipe already exists");
        }

        return userRepository.findById(userId)
                .flatMap(user -> recipeRepository.findById(recipeId)
                        .map(recipe ->{
                            SavedRecipe savedRecipe = new SavedRecipe();
                            savedRecipe.setUser(user);
                            savedRecipe.setRecipe(recipe);
                            return savedRecipeRepository.save(savedRecipe);
                        }))
                .orElseThrow(() -> new IllegalArgumentException("User or Recipe not found"));
    }

    @Override
    @Transactional
    public void removeSavedRecipe(UUID userId, int recipeId) {
        Optional<SavedRecipe> existingSavedRecipe = savedRecipeRepository.findByUserUserIdAndRecipeRecipeId(userId, recipeId);
        existingSavedRecipe.ifPresentOrElse(
                savedRecipeRepository::delete,
                ()-> { throw new IllegalArgumentException("Saved recipe not found"); }
        );

    }

    @Override
    public List<SavedRecipe> getSavedRecipes(UUID userId) {
        return savedRecipeRepository.findAllByUserUserId(userId);
    }

    @Override
    public boolean isSavedRecipe(UUID userId, int recipeId) {
        return savedRecipeRepository.existsByUserUserIdAndRecipeRecipeId(userId, recipeId);
    }

    @Override
    @Transactional
    public void clearAllSavedRecipes(UUID userId) {
        savedRecipeRepository.deleteAllByUserUserId(userId);
    }
}
