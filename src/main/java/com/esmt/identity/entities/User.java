package com.esmt.identity.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @Column(nullable = false)
    private boolean isFirstLogin = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    //pour activer ou désactiver un compte
    //par defaut un compte est actif jusqu'à ce que l'admin le désactive
    private boolean enabled = true;


}
