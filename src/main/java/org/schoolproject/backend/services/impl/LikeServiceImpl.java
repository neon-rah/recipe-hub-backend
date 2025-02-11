package org.schoolproject.backend.services.impl;

import jakarta.transaction.Transactional;
import org.schoolproject.backend.entities.Like;
import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.entities.User;
import org.schoolproject.backend.repositories.LikeRepository;
import org.schoolproject.backend.repositories.RecipeRepository;
import org.schoolproject.backend.repositories.UserRepository;
import org.schoolproject.backend.services.LikeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public LikeServiceImpl(LikeRepository likeRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    @Transactional
    public Like createLike(UUID userId, int recipeId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        Optional<Like> existingLike = likeRepository.findByUserUserIdAndRecipeRecipeId(userId, recipeId);

        if(existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return null;
        }else{
            Like like = new Like();
            like.setUser(user);
            like.setRecipe(recipe);
            return likeRepository.save(like);
        }
    }

    @Override
    @Transactional
    public void deleteLike(UUID userId, int recipeId) {
        Like like = likeRepository.findByUserUserIdAndRecipeRecipeId(userId, recipeId)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);
    }

    @Override
    public boolean isLikedByUser(UUID userId, int recipeId) {
        return likeRepository.existsByUserUserIdAndRecipeRecipeId(userId, recipeId);
    }

    @Override
    public List<Like> getLikesByUser(UUID userId) {
        return likeRepository.findAllByUserUserId(userId);
    }

    @Override
    public List<Like> getLikesByRecipe(int recipeId) {
        return likeRepository.findAllByRecipeRecipeId(recipeId);
    }

    @Override
    public int getLikeCountByRecipe(int recipeId) {
        return likeRepository.countByRecipeRecipeId(recipeId);
    }
}
