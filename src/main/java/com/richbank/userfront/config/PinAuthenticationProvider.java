package com.richbank.userfront.config;

import com.richbank.userfront.service.AccountService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class PinAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final AccountService accountService; // Use the service for account validation

    public PinAuthenticationProvider(UserDetailsService userDetailsService, AccountService accountService) {
        this.userDetailsService = userDetailsService;
        this.accountService = accountService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            // Parse cardNumber and pin from the Authentication object
            int cardNumber = Integer.parseInt(authentication.getName()); // Card number as the username
            int pin = Integer.parseInt(authentication.getCredentials().toString()); // PIN from credentials

            // Validate the card number and PIN using the service
            if (accountService.validateCardNumberAndPin(cardNumber, pin)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(cardNumber)); // Load user by card number
                return new UsernamePasswordAuthenticationToken(userDetails, pin, userDetails.getAuthorities());
            }

            throw new AuthenticationException("Invalid card number or PIN.") {};
        } catch (NumberFormatException e) {
            // Handle case where cardNumber or pin is not a valid number
            throw new AuthenticationException("Invalid card number or PIN format.", e) {};
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
