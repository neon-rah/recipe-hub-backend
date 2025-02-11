package org.schoolproject.backend.specifications;

import org.schoolproject.backend.entities.Recipe;
import org.springframework.data.jpa.domain.Specification;

public class RecipeSpecification {

    public static Specification<Recipe> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
    public static Specification<Recipe> hasRegion(String region) {
        return (root, query, criteriaBuilder) ->
                region == null ? null : criteriaBuilder.equal(root.get("region"), region);
    }
    public static Specification<Recipe> hasIngredient(String ingredient) {
        return (root, query, criteriaBuilder) ->
                ingredient == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("ingredient")), "%" + ingredient.toLowerCase() + "%");
    }
    public static Specification<Recipe> hasCategory (String category ) {
        return (root, query, criteriaBuilder) ->
                category == null ? null : criteriaBuilder.equal(root.get("category"), category);
    }
}
