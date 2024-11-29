package com.richbank.userfront.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class PinAuthenticationController {

    @PostMapping("/pin")
    public ResponseEntity<String> authenticateWithPin(
            @RequestParam int cardNumber,
            @RequestParam String pin) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(cardNumber, pin);
        try {
            // Manually authenticate using the PIN Authentication Provider
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok("Authentication successful");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid card number or PIN");
        }
    }
}
