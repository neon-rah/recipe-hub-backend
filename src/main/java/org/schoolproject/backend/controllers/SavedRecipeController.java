package org.schoolproject.backend.controllers;

import org.schoolproject.backend.config.JwtUtil;
import org.schoolproject.backend.entities.SavedRecipe;
import org.schoolproject.backend.services.SavedRecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/saved-recipes")
public class SavedRecipeController {

    private final SavedRecipeService savedRecipeService;
    private final JwtUtil jwtUtil;

    public SavedRecipeController(SavedRecipeService savedRecipeService, JwtUtil jwtUtil) {
        this.savedRecipeService = savedRecipeService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<SavedRecipe> toggleSavedRecipe(@PathVariable int recipeId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        SavedRecipe savedRecipe = savedRecipeService.toggleSavedRecipe(userId, recipeId);
        return savedRecipe != null ?
                ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe) :
                ResponseEntity.ok(null); // null signifie désenregistrement
    }

    @DeleteMapping("/recipe/{recipeId}")
    public ResponseEntity<Void> removeSavedRecipe(@PathVariable int recipeId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        savedRecipeService.removeSavedRecipe(userId, recipeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SavedRecipe>> getSavedRecipes(HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        List<SavedRecipe> savedRecipes = savedRecipeService.getSavedRecipes(userId);
        return ResponseEntity.ok(savedRecipes);
    }

    @GetMapping("/exists/{recipeId}")
    public ResponseEntity<Boolean> isRecipeSaved(@PathVariable int recipeId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        boolean exists = savedRecipeService.isSavedRecipe(userId, recipeId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearAllSavedRecipes(HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        savedRecipeService.clearAllSavedRecipes(userId);
        return ResponseEntity.noContent().build();
    }


}








/*
package org.schoolproject.backend.controllers;

import org.schoolproject.backend.entities.SavedRecipe;
import org.schoolproject.backend.services.SavedRecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/saved-recipes")
public class SavedRecipeController {
    private final SavedRecipeService savedRecipeService;

    public SavedRecipeController(SavedRecipeService savedRecipeService) {
        this.savedRecipeService = savedRecipeService;
    }

    @PostMapping("/{userId}/{recipeId}")
    public ResponseEntity<SavedRecipe> addSavedRecipe(@PathVariable UUID userId,
                                                      @PathVariable int recipeId) {
        try {
            SavedRecipe savedRecipe = savedRecipeService.toggleSavedRecipe(userId, recipeId);
            return ResponseEntity.ok(savedRecipe);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{userId}/{recipeId}")
    public ResponseEntity<Void> removeSavedRecipe(@PathVariable UUID userId,
                                                  @PathVariable int recipeId) {
        try {
            savedRecipeService.removeSavedRecipe(userId, recipeId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<SavedRecipe>> getSavedRecipes(@PathVariable UUID userId) {
        List<SavedRecipe> savedRecipes = savedRecipeService.getSavedRecipes(userId);
        return ResponseEntity.ok(savedRecipes);
    }


    @GetMapping("/{userId}/exists/{recipeId}")
    public ResponseEntity<Boolean> isRecipeSaved(@PathVariable UUID userId,
                                                 @PathVariable int recipeId) {
        boolean exists = savedRecipeService.isSavedRecipe(userId, recipeId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearAllSavedRecipes(@PathVariable UUID userId) {
        savedRecipeService.clearAllSavedRecipes(userId);
        return ResponseEntity.noContent().build();
    }
}
*/
