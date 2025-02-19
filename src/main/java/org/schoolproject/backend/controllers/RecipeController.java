package org.schoolproject.backend.controllers;

import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.services.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")

public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Recipe> createRecipe(
            @RequestPart("recipe") Recipe recipe,
            @RequestPart(value = "recipeImage", required = false) MultipartFile recipeImage) {
        try{
            return ResponseEntity.ok(recipeService.createRecipe(recipe, recipeImage));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> findRecipeById(@PathVariable int id) {
        return recipeService.findRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<Recipe>> findAllRecipes() {
        return ResponseEntity.ok(recipeService.findAllRecipes());
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recipe>> findRecipeByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(recipeService.findRecipesByUserId(userId));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Recipe> updateRecipe(@PathVariable int id, @RequestPart("recipe") Recipe recipe, @RequestPart(value = "recipeImage", required = false) MultipartFile recipeImage) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipe, recipeImage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String ingredient,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(recipeService.searchRecipes(title, region, ingredient, category));
    }
}
