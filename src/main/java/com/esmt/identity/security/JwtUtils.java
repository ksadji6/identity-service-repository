package com.esmt.identity.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    //secret_ley
    @Value("${jwt.secret:CIS_2026_Secure_Key_Super_Secret_Esmt_M2_Security}")
    private String jwtSecret;

    //expiration = 24h
    @Value("${jwt.expiration:86400000}")
    private int jwtExpirationMs;

    /*private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }*/
    private Key getSigningKey() {
        // La même chaîne exacte de 32 caractères
        String forceSecret = "CIS_2026_Key_CIS_2026_Key_CIS_2026_Key_";
        return Keys.hmacShaKeyFor(forceSecret.getBytes());
    }

    //genere un token avec le role et l'id
    public String generateToken(String email, String role, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role",role)
                .claim("userId",userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .setIssuer("identity-service")
                .signWith(getSigningKey(),  SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //validation du token avec les logs
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        }
        catch (SecurityException | MalformedJwtException e) {
            log.error("Signature JWT invalide: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expiré: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Format JWT non supporté: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims JWT vides: {}", e.getMessage());
        }
        return false;
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

}
