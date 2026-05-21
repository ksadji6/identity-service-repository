package com.esmt.identity.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    public void sendTemporaryPassword(String to, String prenom, String temporaryPassword) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("Bienvenue chez CIS Integration - Vos accès");
        mailMessage.setText("Bonjour " + prenom + ",\n\n" +
                "Votre compte a été créé par l'administrateur.\n" +
                "Voici vos identifiants temporaires :\n" +
                "Email : " + to + "\n" +
                "Mot de passe : " + temporaryPassword + "\n\n" +
                "Vous devrez changer obligatoirement ce mot de passe lors de votre première connexion.");
        mailSender.send(mailMessage);
    }
}
