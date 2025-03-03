package org.schoolproject.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.schoolproject.backend.config.JwtUtil;
import org.schoolproject.backend.dto.RecipeDTO;
import org.schoolproject.backend.dto.RecipeFormDTO;
import org.schoolproject.backend.mappers.RecipeMapper;
import org.schoolproject.backend.services.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final JwtUtil jwtUtil;
    private final RecipeMapper recipeMapper;

    public RecipeController(RecipeService recipeService, JwtUtil jwtUtil, RecipeMapper recipeMapper) {
        this.recipeService = recipeService;
        this.jwtUtil = jwtUtil;
        this.recipeMapper = recipeMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeDTO> createRecipe(
            @ModelAttribute RecipeFormDTO formDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        RecipeDTO recipeDTO = recipeMapper.toRecipeDTO(formDTO);
        RecipeDTO createdRecipe = recipeService.createRecipe(recipeDTO, image, userId);
        return ResponseEntity.ok(createdRecipe);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeDTO> updateRecipe(
            @PathVariable int id,
            @ModelAttribute RecipeFormDTO formDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        RecipeDTO recipeDTO = recipeMapper.toRecipeDTO(formDTO);
        RecipeDTO updatedRecipe = recipeService.updateRecipe(id, recipeDTO, image, userId);
        return ResponseEntity.ok(updatedRecipe);
    }

    /*@PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<RecipeDTO> createRecipe(
            @RequestPart("recipe") RecipeDTO recipeDTO,
            @RequestPart(value = "recipeImage", required = false) MultipartFile recipeImage) {
        try {
            return ResponseEntity.ok(recipeService.createRecipe(recipeDTO, recipeImage));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable int id,
                                                  @RequestPart("recipe") RecipeDTO recipeDTO,
                                                  @RequestPart(value = "recipeImage", required = false) MultipartFile recipeImage) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipeDTO, recipeImage));
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> findRecipeById(
            @PathVariable int id,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        Optional<RecipeDTO> recipeOptional = recipeService.findRecipeById(id);
        if (recipeOptional.isPresent()) {
            RecipeDTO recipe = recipeOptional.get();
            if (!recipe.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(recipe);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> findAllRecipes() {
        return ResponseEntity.ok(recipeService.findAllRecipes());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDTO>> findRecipeByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(recipeService.findRecipesByUserId(userId));
    }
    @GetMapping("/public")
    public ResponseEntity<Page<RecipeDTO>> getPublicRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);

        Page<RecipeDTO> recipes = recipeService.findRecipesExcludingUser(userId, page, size);
        return ResponseEntity.ok(recipes);
    }

    // RecipeController.java (extrait)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int id, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeDTO>> searchRecipes(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String ingredient,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(recipeService.searchRecipes(title, ingredient, category));
    }

    // RecipeController.java
    @GetMapping("/public/search")
    public ResponseEntity<Page<RecipeDTO>> searchPublicRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam String query,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractToken(request);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid JWT token");
        }
        UUID userId = jwtUtil.extractUserId(token);
        Page<RecipeDTO> recipes = recipeService.searchRecipesExcludingUser(userId, query, page, size);
        return ResponseEntity.ok(recipes);
    }

    // récupérer les recettes avec les informations de l'utilisateur
    @GetMapping("/user-info/{userId}")
    public ResponseEntity<List<RecipeDTO>> findRecipesWithUserInfo(@PathVariable UUID userId) {
        return ResponseEntity.ok(recipeService.findRecipesWithUserInfo(userId));
    }
}
