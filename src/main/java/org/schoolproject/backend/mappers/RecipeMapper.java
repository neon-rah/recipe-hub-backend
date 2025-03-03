package org.schoolproject.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.schoolproject.backend.dto.RecipeDTO;
import org.schoolproject.backend.dto.RecipeFormDTO;
import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.entities.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(target = "id", source = "idRecipe")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "userId" , source = "user.idUser")
    RecipeDTO toDto(Recipe recipe);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "userId", ignore = true)
    RecipeDTO toRecipeDTO(RecipeFormDTO formDTO);
//    @Mapping(target = "userId", source = "user.idUser")

    @Mapping(target = "idRecipe", source = "id")
    @Mapping(target = "user", ignore = true)
    Recipe toEntity(RecipeDTO recipeDTO);

    @Mapping(target = "idUser", source = "idUser")
    @Mapping(target = "recipes", ignore = true) // Ignorer explicitement recipes
    UserDTO toDto(User user);


//    @Named("mapRecipesSafely")
//    default List<RecipeDTO> mapRecipesSafely(List<Recipe> recipes, RecipeMapper recipeMapper) {
//        return recipes != null ? recipes.stream().map(recipeMapper::toDto).toList() : Collections.emptyList();
//    }
}
