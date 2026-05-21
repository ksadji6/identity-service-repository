package com.esmt.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }

    /*@Bean
    CommandLineRunner start(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setPrenom("Admin"); // Ajoute ça
                admin.setNom("CIS");      // Ajoute ça
                admin.setEmail("admin@cis.sn");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                admin.setFirstLogin(false);
                userRepository.save(admin);
                System.out.println("COMPTE ADMIN CRÉÉ AVEC SUCCÈS !");
            }
        };
    }*/

}
