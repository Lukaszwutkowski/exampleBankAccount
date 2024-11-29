package com.richbank.userfront.service.UserServiceImpl;

import com.richbank.userfront.dao.PrimaryAccountDao;
import com.richbank.userfront.dao.SavingsAccountDao;
import com.richbank.userfront.domain.*;
import com.richbank.userfront.service.AccountService;
import com.richbank.userfront.service.TransactionService;
import com.richbank.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService {

    private static int nextAccountNumber = 11442264;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Validates the provided card number and PIN by checking the database.
     *
     * @param cardNumber the card number provided by the user
     * @param pin        the PIN provided by the user
     * @return true if the card number and PIN match a record in the database, false otherwise
     */
    @Override
    public boolean validateCardNumberAndPin(int cardNumber, int pin) {
        // Ensure inputs are valid
        if (cardNumber <= 0 || pin <= 0) { // Validate that the values are greater than zero
            return false;
        }

        // Fetch account from the database based on the card number
        PrimaryAccount account = primaryAccountDao.findByCardNumber(cardNumber);

        // Validate if account exists and the PIN matches
        if (account != null) {
            int storedPin = account.getPin(); // Assuming `PrimaryAccount` has a `pin` field
            return storedPin == pin; // Compare stored PIN with the provided one
        }

        return false; // Return false if no account found or validation fails
    }

    @Override
    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(BigDecimal.ZERO);
        primaryAccount.setAccountNumber(accountGen());
        primaryAccount.setCardNumber(generateUniqueCardNumber());
        primaryAccount.setPin(generateRandomPin());


        primaryAccountDao.save(primaryAccount);

        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());

    }

    @Override
    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(BigDecimal.ZERO);
        savingsAccount.setAccountNumber(accountGen());

        return savingsAccountDao.save(savingsAccount);
    }

    @Override
    public void deposit(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryDepositTransaction(primaryTransaction);

        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to Savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsDepositTransaction(savingsTransaction);

        }
    }

    @Override
    public void withdraw(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryWithdrawTransaction(primaryTransaction);

        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from Savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsWithdrawTransaction(savingsTransaction);

        }
    }

    private int accountGen() {
        return ++nextAccountNumber;
    }

    /**
     * Generates a unique 10-digit card number.
     *
     * @return a unique card number for the account
     */
    private int generateUniqueCardNumber() {
        Random random = new Random();
        int cardNumber;

        // Ensure the card number is unique by checking in the database
        do {
            cardNumber = 100000000 + random.nextInt(900000000); // Generate a 10-digit number
        } while (primaryAccountDao.findByCardNumber(cardNumber) != null);

        return cardNumber;
    }

    /**
     * Generates a random 4-digit numeric PIN.
     *
     * @return a randomly generated 4-digit PIN as an integer
     */
    private int generateRandomPin() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // Generates a number between 1000 and 9999
    }



}
