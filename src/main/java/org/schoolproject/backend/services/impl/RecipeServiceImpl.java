package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.dto.RecipeDTO;
import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.mappers.RecipeMapper;
import org.schoolproject.backend.repositories.RecipeRepository;
import org.schoolproject.backend.services.FileStorageService;
import org.schoolproject.backend.services.NotificationService;
import org.schoolproject.backend.services.RecipeService;
import org.schoolproject.backend.specifications.RecipeSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final FileStorageService fileStorageService;
    private final RecipeMapper recipeMapper;
    private final NotificationService notificationService;

    public RecipeServiceImpl(RecipeRepository recipeRepository, FileStorageService fileStorageService, RecipeMapper recipeMapper, NotificationService notificationService) {
        this.recipeRepository = recipeRepository;
        this.fileStorageService = fileStorageService;
        this.recipeMapper = recipeMapper;
        this.notificationService = notificationService;
    }

    @Override
    public RecipeDTO createRecipe(RecipeDTO recipeDTO, MultipartFile recipeImage) {
        String imgUrl = null;

        if (recipeImage != null && !recipeImage.isEmpty()) {
            imgUrl = fileStorageService.storeFile(recipeImage, "recipe", null);
        }

        // Convertir le DTO en entité
        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        recipe.setImage(imgUrl);

        // Sauvegarder l'entité et retourner le DTO
        Recipe savedRecipe = recipeRepository.save(recipe);

        notificationService.sendRecipePublicationNotification(recipe.getUser().getIdUser(),recipe.getIdRecipe(), recipe.getTitle());


        return recipeMapper.toDto(savedRecipe);
    }

    @Override
    public Optional<RecipeDTO> findRecipeById(int recipeId) {
        return recipeRepository.findById(recipeId).map(recipeMapper::toDto);
    }

    @Override
    public List<RecipeDTO> findAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDTO> findRecipesByUserId(UUID userId) {
        return recipeRepository.findAllByUserIdUser(userId).stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecipeDTO updateRecipe(int recipeId, RecipeDTO updatedRecipeDTO, MultipartFile newRecipeImage) {
        return recipeRepository.findById(recipeId).map(existingRecipe -> {
            existingRecipe.setTitle(updatedRecipeDTO.getTitle());
            existingRecipe.setDescription(updatedRecipeDTO.getDescription());
            existingRecipe.setCategory(updatedRecipeDTO.getCategory());
            existingRecipe.setRegion(updatedRecipeDTO.getRegion());
            existingRecipe.setIngredients(updatedRecipeDTO.getIngredients());
            existingRecipe.setPreparation(updatedRecipeDTO.getPreparation());

            if (newRecipeImage != null && !newRecipeImage.isEmpty()) {
                String newImgUrl = fileStorageService.storeFile(newRecipeImage, "recipe", existingRecipe.getImage());
                existingRecipe.setImage(newImgUrl);
            }

            return recipeMapper.toDto(recipeRepository.save(existingRecipe));
        }).orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
    }

    @Override
    @Transactional
    public void deleteRecipe(int recipeId) {
        recipeRepository.findById(recipeId).ifPresentOrElse(
                recipe -> {
                    if (recipe.getImage() != null) {
                        fileStorageService.deleteFile(recipe.getImage());
                    }
                    recipeRepository.deleteById(recipeId);
                }, () -> {
                    throw new IllegalArgumentException("Recipe not found");
                });
    }

    @Override
    public List<RecipeDTO> searchRecipes(String title, String region, String ingredient, String category) {
        Specification<Recipe> spec = Specification
                .where(RecipeSpecification.hasTitle(title))
                .and(RecipeSpecification.hasRegion(region))
                .and(RecipeSpecification.hasIngredient(ingredient))
                .and(RecipeSpecification.hasCategory(category));

        return recipeRepository.findAll(spec).stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    // récupérer des recettes avec les informations de l'utilisateur propriétaire
    @Override
    public List<RecipeDTO> findRecipesWithUserInfo(UUID userId) {
        List<Recipe> recipes = recipeRepository.findAllByUserIdUser(userId);
        return recipes.stream()
                .map(recipe -> {
                    RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

                    return recipeDTO;
                })
                .collect(Collectors.toList());
    }
}
