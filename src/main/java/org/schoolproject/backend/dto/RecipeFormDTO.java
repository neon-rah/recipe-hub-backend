package org.schoolproject.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeFormDTO {
    private String title;
    private String description;
    private String ingredients;
    private String preparation;
    private String category;
}
