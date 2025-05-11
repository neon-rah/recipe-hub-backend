package org.schoolproject.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID idUser;
    private String lastName;
    private String firstName;
    private String email;
    @JsonIgnore
    private String password;
    private String address;
    private String profilePic;
    private LocalDateTime created;

    private String resetToken;
    private LocalDateTime resetTokenExpiredAt;

    @JsonIgnore
    @JsonManagedReference  // Empêche la boucle infinie
    private List<RecipeDTO> recipes;  // Champ facultatif
}
