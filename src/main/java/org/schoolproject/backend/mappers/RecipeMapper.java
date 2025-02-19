package org.schoolproject.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.schoolproject.backend.dto.RecipeDTO;
import org.schoolproject.backend.entities.Recipe;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(target = "user", ignore = true)
    RecipeDTO toDto(Recipe recipe);
    Recipe toEntity(RecipeDTO recipeDTO);
//    @Named("mapRecipesSafely")
//    default List<RecipeDTO> mapRecipesSafely(List<Recipe> recipes, RecipeMapper recipeMapper) {
//        return recipes != null ? recipes.stream().map(recipeMapper::toDto).toList() : Collections.emptyList();
//    }
}
