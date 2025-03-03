package org.schoolproject.backend.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDTO {
    private int id;
    private String title;
    private String description; 
    private String ingredients;
    private String preparation;
    private String category;
//    private String region;
    private String image;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private UUID userId;


//    @JsonBackReference  // Empêche la boucle infinie
//    @JsonIgnore
    private UserDTO user; // Réutilisation de UserDTO, les informations de l'utilisateur qui a publier l'offre
}
