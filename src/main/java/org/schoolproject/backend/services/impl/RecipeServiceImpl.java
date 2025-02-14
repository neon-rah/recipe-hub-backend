package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.repositories.RecipeRepository;
import org.schoolproject.backend.services.RecipeService;
import org.schoolproject.backend.specifications.RecipeSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Recipe createRecipe(Recipe recipe) {
       return recipeRepository.save(recipe);
    }

    @Override
    public Optional<Recipe> findRecipeById(int recipeId) {
        return recipeRepository.findById(recipeId);
    }

    @Override
    public List<Recipe> findAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> findRecipesByUserId(UUID userId) {
        return recipeRepository.findAllByUserIdUser(userId);
    }

    @Override
    @Transactional
    public Recipe updateRecipe(int recipeId, Recipe updatedRecipe) {
        return recipeRepository.findById(recipeId).map(
                existingRecipe ->{
                    existingRecipe.setTitle(updatedRecipe.getTitle());
                    existingRecipe.setDescription(updatedRecipe.getDescription());
                    existingRecipe.setCategory(updatedRecipe.getCategory());
                    existingRecipe.setRegion(updatedRecipe.getRegion());
                    existingRecipe.setIngredients(updatedRecipe.getIngredients());
                    existingRecipe.setPreparation(updatedRecipe.getPreparation());
                    existingRecipe.setImage(updatedRecipe.getImage());

                    return recipeRepository.save(existingRecipe);
                }
        ).orElseThrow(()-> new IllegalArgumentException("Recipe not found"));
    }

    @Override
    @Transactional
    public void deleteRecipe(int recipeId) {
        if(!recipeRepository.existsById(recipeId)) {
            throw new IllegalArgumentException("Recipe not found");
        }
        recipeRepository.deleteById(recipeId);
    }


    @Override
    public List<Recipe> searchRecipes(String title, String region, String ingredient, String category) {
        Specification<Recipe> spec = Specification
                .where(RecipeSpecification.hasTitle(title))
                .and(RecipeSpecification.hasRegion(region))
                .and(RecipeSpecification.hasIngredient(ingredient))
                .and(RecipeSpecification.hasCategory(category));

        return recipeRepository.findAll(spec);
    }
}
