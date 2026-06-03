package com.esmt.identity.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "users")
@Data @NoArgsConstructor
@AllArgsConstructor @Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    //first connection security
    @Column(name = "is_first_login",nullable = false)
    @JsonProperty("firstLogin")
    @Getter(value = lombok.AccessLevel.PUBLIC)
    private Boolean isFirstLogin = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    //pour activer ou désactiver un compte
    //par defaut un compte est actif jusqu'à ce que l'admin le désactive

    @Column(nullable = false)
    @JsonProperty("enabled")
    @Getter(value = lombok.AccessLevel.PUBLIC)
    private Boolean enabled = true;


}
