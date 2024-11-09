package com.richbank.userfront.controller;

import com.richbank.userfront.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Endpoint to process an external payment based on card number.
     *
     * @param cardNumber the card number to identify the primary account
     * @param amount     the amount to be deducted
     * @return a response indicating whether the payment was successful or not
     */
    @PostMapping("/make")
    public ResponseEntity<String> makePayment(
            @RequestParam int cardNumber,
            @RequestParam BigDecimal amount) {

        boolean success = transactionService.processExternalPayment(cardNumber, amount);

        if (success) {
            return ResponseEntity.ok("Payment processed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Insufficient funds or invalid card number.");
        }
    }
}
