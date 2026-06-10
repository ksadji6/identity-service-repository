    package com.esmt.identity.controllers;

    import com.esmt.identity.entities.Role;
    import com.esmt.identity.entities.User;
    import com.esmt.identity.services.UserService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/users")
    @RequiredArgsConstructor
    @Tag(name = "Utilisateurs", description = "Gestion des comptes collaborateurs et des rôles")
    public class UserController {
        private final UserService userService;
        //acces admin only
        @PostMapping("/create")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Créer un compte (Admin)", description = "Permet à l'administrateur de créer un nouvel utilisateur avec un mot de passe temporaire")
        public ResponseEntity<Map<String, Object>> createByAdmin(@Valid @RequestBody User user) {
            User saved = userService.createAccountByAdmin(user);
            return ResponseEntity.ok(Map.of(
                    "id", saved.getId(),
                    "email", saved.getEmail(),
                    "message", "Compte créé avec succès"
            ));
        }/*public ResponseEntity<User> createByAdmin(@Valid @RequestBody User user) {
            return ResponseEntity.ok(userService.createAccountByAdmin(user));
        }*/

        @GetMapping("/email/{email}")
        @PreAuthorize("hasAnyRole('ADMIN','INGENIEUR', 'CHEF_PROJET', 'PRESALES', 'SUPERVISEUR')")
        @Operation(summary = "Rechercher un utilisateur par email", description = "Récupère les détails d'un profil spécifique")
        public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
            // Utile pour vérifier si un ingénieur existe chez CIS
            return ResponseEntity.ok(userService.findByEmail(email));
        }

        @GetMapping("/all")
        @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PROJET', 'PRESALES', 'SUPERVISEUR', 'INGENIEUR')")
        @Operation(summary = "Lister tous les utilisateurs", description = "Accès réservé aux administrateurs pour la vue d'ensemble du personnel")
        public ResponseEntity<List<User>> getAllUsers() {
            System.out.println("DEBUG - Accès à /api/users/all par l'utilisateur connecté");
            return ResponseEntity.ok(userService.getAllUsers());
        }
    
        @PutMapping("/{id}/role")
        @PreAuthorize("hasAnyRole('ADMIN')")
        @Operation(summary = "Modifier le rôle d'un utilisateur", description = "Permet de promouvoir un collaborateur (ex: INGENIEUR -> CHEF_PROJET)")
        public ResponseEntity<User> changeRole(@PathVariable Long id, @RequestParam Role role) {
        return  ResponseEntity.ok(userService.updateRole(id, role));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN')")
        @Operation(summary = "Modifier les informations d'un utilisateur")
        public ResponseEntity<User> changeInformations(@PathVariable Long id, @RequestBody User newUser) {
            return ResponseEntity.ok(userService.updateUser(id, newUser));
        }

        @DeleteMapping("/delete/{id}")
        @PreAuthorize("hasAnyRole('ADMIN')")
        @Operation(summary = "Supprimer un compte utilisateur")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/id/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'INGENIEUR', 'CHEF_PROJET', 'PRESALES', 'SUPERVISEUR')")
        @Operation(summary = "Récupérer un utilisateur par son ID")
        public ResponseEntity<User> getUserById(@PathVariable Long id) {
            return ResponseEntity.ok(userService.findById(id));
        }

        @DeleteMapping("/disable/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Désactiver un compte utilisateur")
        public ResponseEntity<Void> disableAccount(@PathVariable Long id) {
            userService.disableUser(id);
            return ResponseEntity.noContent().build();
        }

        @PutMapping("/enable/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Réactiver un compte utilisateur")
        public ResponseEntity<Void> enableAccount(@PathVariable Long id) {
            userService.enableUser(id);
            return ResponseEntity.ok().build();
        }

        @GetMapping("/exists/{id}")
        public boolean checkUserExists(@PathVariable Long id) {
            try {
                return userService.findById(id) != null;
            } catch (Exception e) {
                return false;
            }
        }

        @GetMapping("/verify/{id}/{requiredRole}")
        public ResponseEntity<Boolean> verifyUserRole(@PathVariable Long id, @PathVariable Role requiredRole) {
            try {
                User user = userService.findById(id);
                // On vérifie si l'utilisateur existe ET s'il a le bon rôle
                return ResponseEntity.ok(user != null && user.getRole().equals(requiredRole));
            } catch (Exception e) {
                return ResponseEntity.ok(false);
            }
        }

    }
