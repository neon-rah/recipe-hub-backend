package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.dto.RecipeDTO;
import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.mappers.RecipeMapper;
import org.schoolproject.backend.repositories.RecipeRepository;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.FileStorageService;
import org.schoolproject.backend.services.NotificationService;
import org.schoolproject.backend.services.RecipeService;
import org.schoolproject.backend.services.UserService;
import org.schoolproject.backend.specifications.RecipeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final RecipeMapper recipeMapper;
    private final NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(RecipeServiceImpl.class);

    public RecipeServiceImpl(RecipeRepository recipeRepository, FileStorageService fileStorageService, UserRepository userRepository, RecipeMapper recipeMapper, NotificationService notificationService) {
        this.recipeRepository = recipeRepository;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;

        this.recipeMapper = recipeMapper;
        this.notificationService = notificationService;
    }
    @Override
    public RecipeDTO createRecipe(RecipeDTO recipeDTO, MultipartFile recipeImage, UUID userId) {
        String imgUrl = null;
        if (recipeImage != null && !recipeImage.isEmpty()) {
            imgUrl = fileStorageService.storeFile(recipeImage, "recipe", null);
        }

        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        recipe.setImage(imgUrl);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        recipe.setUser(user);

        Recipe savedRecipe = recipeRepository.save(recipe);

        logger.debug("before entre notif part");

        notificationService.sendRecipePublicationNotification(recipe.getUser().getIdUser(),recipe.getIdRecipe(), recipe.getTitle());

        logger.debug("hors notif part");

        return recipeMapper.toDto(savedRecipe);
    }

    @Override
    @Transactional
    public RecipeDTO updateRecipe(int recipeId, RecipeDTO updatedRecipeDTO, MultipartFile newRecipeImage, UUID userId) {
        // Récupérer l'utilisateur connecté depuis le contexte de sécurité

        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return recipeRepository.findById(recipeId).map(existingRecipe -> {
            if (!existingRecipe.getUser().getIdUser().equals(userId)) {
                throw new SecurityException("You are not authorized to update this recipe");
            }

            existingRecipe.setTitle(updatedRecipeDTO.getTitle());
            existingRecipe.setDescription(updatedRecipeDTO.getDescription());
            existingRecipe.setCategory(updatedRecipeDTO.getCategory());
            existingRecipe.setIngredients(updatedRecipeDTO.getIngredients());
            existingRecipe.setPreparation(updatedRecipeDTO.getPreparation());

            if (newRecipeImage != null && !newRecipeImage.isEmpty()) {
                String newImgUrl = fileStorageService.storeFile(newRecipeImage, "recipe", existingRecipe.getImage());
                existingRecipe.setImage(newImgUrl);
            }

            return recipeMapper.toDto(recipeRepository.save(existingRecipe));
        }).orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
    }
/*
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
    @Transactional
    public RecipeDTO updateRecipe(int recipeId, RecipeDTO updatedRecipeDTO, MultipartFile newRecipeImage) {
        return recipeRepository.findById(recipeId).map(existingRecipe -> {
            existingRecipe.setTitle(updatedRecipeDTO.getTitle());
            existingRecipe.setDescription(updatedRecipeDTO.getDescription());
            existingRecipe.setCategory(updatedRecipeDTO.getCategory());
//            existingRecipe.setRegion(updatedRecipeDTO.getRegion());
            existingRecipe.setIngredients(updatedRecipeDTO.getIngredients());
            existingRecipe.setPreparation(updatedRecipeDTO.getPreparation());

            if (newRecipeImage != null && !newRecipeImage.isEmpty()) {
                String newImgUrl = fileStorageService.storeFile(newRecipeImage, "recipe", existingRecipe.getImage());
                existingRecipe.setImage(newImgUrl);
            }

            return recipeMapper.toDto(recipeRepository.save(existingRecipe));
        }).orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
    }*/

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
        return recipeRepository.findAllByUserIdUserOrderByUpdatedDateDesc(userId).stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
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
    public List<RecipeDTO> searchRecipes(String title, String ingredient, String category) {
        Specification<Recipe> spec = Specification
                .where(RecipeSpecification.hasTitle(title))
//                .and(RecipeSpecification.hasRegion(region))
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

    @Override
    public Page<RecipeDTO> findRecipesExcludingUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedDate").descending());
        Page<Recipe> recipes = recipeRepository.findAllByUserIdUserNotWithUser(userId, pageable);

        return recipes.map(recipeMapper::toDto);
    }

    @Override
    public Page<RecipeDTO> searchRecipesExcludingUser(UUID userId, String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedDate").descending());
        Page<Recipe> recipes = recipeRepository.findByUserIdUserNotAndTitleOrIngredientsContainingIgnoreCase(userId, query, pageable);
        return recipes.map(recipeMapper::toDto);
    }

    @Override
    public Page<RecipeDTO> findRecipesExcludingUserByCategory(UUID userId, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedDate").descending());
        if (category == null || category.equals("All")) {
            return findRecipesExcludingUser(userId, page, size); // Sans filtre si "All"
        }
        Page<Recipe> recipes = recipeRepository.findByUserIdUserNotAndCategory(userId, category, pageable);
        return recipes.map(recipeMapper::toDto);
    }

    @Override
    public Page<RecipeDTO> searchRecipesExcludingUserByCategory(UUID userId, String query, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedDate").descending());
        if (category == null || category.equals("All")) {
            return searchRecipesExcludingUser(userId, query, page, size); // Sans filtre si "All"
        }
        Page<Recipe> recipes = recipeRepository.findByUserIdUserNotAndTitleOrIngredientsContainingIgnoreCaseAndCategory(
                userId, query, category, pageable);
        return recipes.map(recipeMapper::toDto);


    }

    @Override
    public RecipeDTO getRandomRecipeExcludingUser(UUID userId) {
        List<Recipe> recipes = recipeRepository.findAllByUserIdUserNot(userId);
        if (recipes.isEmpty()) throw new IllegalStateException("No recipes available");
        Recipe randomRecipe = recipes.get(new Random().nextInt(recipes.size()));
        return recipeMapper.toDto(randomRecipe);
    }
}
