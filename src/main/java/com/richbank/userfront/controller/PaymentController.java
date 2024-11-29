package com.richbank.userfront.controller;

import com.richbank.userfront.domain.User;
import com.richbank.userfront.service.AccountService;
import com.richbank.userfront.service.TransactionService;
import com.richbank.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final AccountService accountService;
    private final UserService userService;
    private final TransactionService transactionService;

    @Autowired
    public PaymentController(AccountService accountService, UserService userService, TransactionService transactionService) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @PostMapping("/make")
    public ResponseEntity<?> makePayment(@RequestParam int cardNumber, @RequestParam("amount") BigDecimal amount) {

        // Call the service directly
        boolean success = transactionService.processExternalPayment(cardNumber, amount);
        return success ? ResponseEntity.ok("Payment successful.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed.");
    }

}
