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

    @PostMapping("/make")
    public ResponseEntity<String> makePayment(@RequestParam String accountType,
                                              @RequestParam String accountNumber,
                                              @RequestParam BigDecimal amount) {
        try {
            boolean success = transactionService.processExternalPayment(accountType, accountNumber, amount);
            if (success) {
                return ResponseEntity.ok("Payment processed successfully.");
            } else {
                return ResponseEntity.badRequest().body("Insufficient funds or invalid account.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Payment processing failed: " + e.getMessage());
        }
    }
}
