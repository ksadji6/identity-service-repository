package com.esmt.identity.dtos;

import com.esmt.identity.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String prenom;
    private String nom;
    private Role role;
    private Boolean isFirstLogin; //ca sera pour declencher le popup


}
