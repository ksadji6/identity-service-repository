package com.esmt.identity.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException e){
        Map<String,String> error = new HashMap<>();
        error.put("message",e.getMessage());
        error.put("status","404");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler (Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e){
        //temporaire
        e.printStackTrace();
        Map<String,String> error = new HashMap<>();
        error.put("error, Une erreur interne est survenue.", e.getMessage());
        //error.put("error", "Une erreur interne est survenue.");
        error.put("status", "500");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException e){
        Map<String,String> error = new HashMap<>();
        error.put("message", "Email ou mot de passe incorrect");
        error.put("status", "401");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
