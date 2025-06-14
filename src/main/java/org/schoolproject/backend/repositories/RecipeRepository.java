package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Recipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository

public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {

    List<Recipe> findAllByUserIdUser(UUID userId);
    Page<Recipe> findAllByUserIdUserNot(UUID userId, Pageable pageable);
    @Query("SELECT r FROM Recipe r JOIN FETCH r.user WHERE r.user.idUser != :userId")
    Page<Recipe> findAllByUserIdUserNotWithUser(UUID userId, Pageable pageable);
    List<Recipe> findAllByCategory(String category);

    @Query("SELECT r FROM Recipe r WHERE r.user.idUser != :userId AND " +
            "(LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.ingredients) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Recipe> findByUserIdUserNotAndTitleOrIngredientsContainingIgnoreCase(UUID userId, String query, Pageable pageable);


    // Nouvelle méthode pour filtrer par catégorie
    Page<Recipe> findByUserIdUserNotAndCategory(UUID userId, String category, Pageable pageable);

    // Nouvelle méthode pour recherche + catégorie
    @Query("SELECT r FROM Recipe r WHERE r.user.idUser != :userId AND " +
            "(LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.ingredients) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "r.category = :category")
    Page<Recipe> findByUserIdUserNotAndTitleOrIngredientsContainingIgnoreCaseAndCategory(
            UUID userId, String query, String category, Pageable pageable);

    List<Recipe> findAllByUserIdUserNot(UUID userId);

    // Nouvelle méthode avec tri par updatedDate DESC
    List<Recipe> findAllByUserIdUserOrderByUpdatedDateDesc(UUID userId);
}
