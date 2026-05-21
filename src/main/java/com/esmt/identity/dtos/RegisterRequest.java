package com.esmt.identity.dtos;

import com.esmt.identity.entities.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private Role role;
}
