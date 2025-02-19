package org.schoolproject.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.schoolproject.backend.dto.RecipeDTO;
import org.schoolproject.backend.dto.UserDTO;
import org.schoolproject.backend.entities.Recipe;
import org.schoolproject.backend.entities.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

//    @Mapping(target = "recipes", source = "recipes", qualifiedByName = "mapRecipesSafely")
//    @Mapping(target = "recipes", source = "recipes")
//    UserDTO toDtoWithRecipes(User user);

    @Mapping(target = "recipes", ignore = true)
    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);


}

