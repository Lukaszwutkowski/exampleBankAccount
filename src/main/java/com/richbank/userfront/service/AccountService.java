package com.richbank.userfront.service;

import com.richbank.userfront.domain.PrimaryAccount;
import com.richbank.userfront.domain.SavingsAccount;

import java.math.BigDecimal;
import java.security.Principal;

public interface AccountService {
    PrimaryAccount createPrimaryAccount();
    SavingsAccount createSavingsAccount();
    void deposit(String accountType, double amount, Principal principal);
    void withdraw(String accountType, double amount, Principal principal);
    boolean validateCardNumberAndPin(int cardNumber, int pin);
}
