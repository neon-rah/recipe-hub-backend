package org.schoolproject.backend.services;

import org.schoolproject.backend.entities.Like;

import java.util.List;
import java.util.UUID;

public interface LikeService {
    Like toggleLike(UUID userId, int recipeId);
    void deleteLike(UUID userId, int recipeId);
    boolean isLikedByUser(UUID userId, int recipeId);
    List<Like> getLikesByUser(UUID userId);
    List<Like> getLikesByRecipe(int recipeId);

    int getLikeCountByRecipe(int recipeId);


}
