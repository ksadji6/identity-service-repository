package com.esmt.identity.controllers;

import com.esmt.identity.dtos.AuthResponse;
import com.esmt.identity.dtos.LoginRequest;
import com.esmt.identity.dtos.PasswordChangeRequest;
import com.esmt.identity.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour la connexion et la gestion du mot de passe")
public class AuthController {
    private final UserService userService;

    //creation du compte par l'admin
    @Operation(summary = "Se connecter", description = "Permet d'obtenir un token JWT après vérification des identifiants")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.authenticate(loginRequest));
    }
    //modifier le password
    @Operation(summary = "Changer le mot de passe initial", description = "Action obligatoire lors de la première connexion")
    @PostMapping("/update-password")
    public ResponseEntity<Void> updateFirstPassword(@RequestBody PasswordChangeRequest passwordRequest)
    {
        userService.updatePassword(passwordRequest.getEmail(), passwordRequest.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
