package com.esmt.identity.security;

import com.esmt.identity.entities.User;
import com.esmt.identity.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        //on recup le header "Authorization"
        String authHeader = request.getHeader("Authorization");

        //on verifie s'il contient un bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            //on valide le token via jwtUtils
            if(jwtUtils.validateToken(token)){
                String email = jwtUtils.getEmailFromToken(token);
                //on recup le role
                String role = jwtUtils.getRoleFromToken(token);
                //role avec spring ROLE_
                String springRole = "ROLE_" + role;

                //Verifions l'obligation de changement de mdp cad si isFirstLogin=true
                User user = userRepository.findByEmail(email).orElse(null);

                //on verifie si le user existe
                if (user != null) {
                    //on verifie si c'est actif/enable ou non
                    if (!user.isEnabled()) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"COMPTE_DESACTIVE\", \"message\": \"Votre compte a été suspendu par l'administrateur.\"}");
                        return;
                    }
                    if (user.isFirstLogin()) {
                        //s'il ne change pas son mdp dans /update-password
                        if (!request.getRequestURI().contains("/api/auth/update-password")) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"ACTIVATION_REQUISE\", \"message\": \"Veuillez changer votre mot de passe temporaire pour accéder aux services.\"}");
                            return; //on bloque le reste tant qu'il ne change pas le mdp
                        }
                    }

                    //on crée l'objet d'authentification pour Spring uniquement si l'user est valide
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email, null, Collections.singletonList(new SimpleGrantedAuthority(springRole)));
                    //on l'enregistre dans le contexte de securité
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}