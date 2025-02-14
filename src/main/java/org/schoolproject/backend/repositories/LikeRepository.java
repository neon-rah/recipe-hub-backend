package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    // Trouver tous les likes d'une recette
    List<Like> findAllByRecipeIdRecipe(int recipeId);

    // Trouver tous les likes d'un utilisateur
    List<Like> findAllByUserIdUser(UUID userId);

    // Vérifier si un utilisateur a déjà liké une recette
    boolean existsByUserIdUserAndRecipeIdRecipe(UUID userId, int recipeId);

    // Récupérer un like spécifique d'un utilisateur sur une recette
    Optional<Like> findByUserIdUserAndRecipeIdRecipe(UUID userId, int recipeId);

    // Supprimer un like d'un utilisateur sur une recette
    void deleteByUserIdUserAndRecipeIdRecipe(UUID userId, int recipeId);

    // Compter le nombre de likes d'une recette
    int countByRecipeIdRecipe(int recipeId);
}
