package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository

public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {

    List<Recipe> findAllByUserUserId(UUID userId);
    List<Recipe> findAllByCategory(String category);
    List<Recipe> findAllByRegion(String region);
}
