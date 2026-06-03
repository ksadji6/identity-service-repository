package com.esmt.identity.services;

import com.esmt.identity.dtos.AuthResponse;
import com.esmt.identity.dtos.LoginRequest;
import com.esmt.identity.dtos.UserResponse;
import com.esmt.identity.entities.Role;
import com.esmt.identity.entities.User;
import com.esmt.identity.exceptions.UserNotFoundException;
import com.esmt.identity.repositories.UserRepository;
import com.esmt.identity.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //pour le hachage du mdp
    private final JwtUtils jwtUtils;
    private final EmailService emailService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    //methode de creation de compte par l'admin
    public User createAccountByAdmin(User user) {
        String temporaryPassword = user.getPassword();
        //Hachage du mdp temporaire
        user.setPassword(passwordEncoder.encode(temporaryPassword));

        //mettre le isFirstLogin à True
        user.setIsFirstLogin(true);
        User savedUser= userRepository.save(user);

        //envoi du mail avec les accès temporaires
        try {
            emailService.sendTemporaryPassword(savedUser.getEmail(), savedUser.getPrenom(), temporaryPassword);
        }catch (Exception e) {
            // On logue l'erreur, mais on ne bloque pas la création si le mail échoue
            System.err.println("Erreur lors de l'envoi du mail : " + e.getMessage());
        }
        return savedUser;
    }

    //update mdp par le user à  sa first connection
    public User updatePassword(String email, String newPassword) {
        return userRepository.findByEmail(email)
                .map(userToUpdate -> {
                    userToUpdate.setPassword(passwordEncoder.encode(newPassword));
                    userToUpdate.setIsFirstLogin(false);
                    return userRepository.save(userToUpdate);
                })
                .orElseThrow(() -> new UserNotFoundException("Aucun utilisateur avec l'email : " + email));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'email : " + email));
    }

    public AuthResponse authenticate(LoginRequest loginRequest) {
        //find le user
        User user= userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Identifiants incorrects"));
        //check si le mdp est le meme
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Mot de Passe incorrect");
        }
        //on génère le token
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        //transforme l'entité en dto
        UserResponse userDto= UserResponse.builder()
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .isFirstLogin(user.getIsFirstLogin())
                .build();
        return AuthResponse.builder()
                .token(token)
                .user(userDto)
                .build();

    }

    //Liste des users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //changer role d'un user
    public User updateRole(Long id, Role newRole) {
        User user= userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    //maj des infos d'un user
    public User updateUser(Long id, User userDetails) {
        User user= userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));
        user.setNom(userDetails.getNom());
        user.setPrenom(userDetails.getPrenom());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    //desactiver un compte mais pas le supprimer de la base
    public void disableUser(Long id){
        User user = findById(id);
        user.setEnabled(false);//compte suspendu
        userRepository.save(user);
    }

    //reactiver un compte utilisateur
    public void enableUser(Long id) {
        User user = findById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }


    //delete un user definitivement
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));
        userRepository.delete(user);
    }

    //Find By Id
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("ID inconnu"));
    }

}
