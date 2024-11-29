package com.richbank.userfront.service.UserServiceImpl;

import com.richbank.userfront.dao.*;
import com.richbank.userfront.domain.*;
import com.richbank.userfront.service.TransactionService;
import com.richbank.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private PrimaryTransactionDao primaryTransactionDao;

    @Autowired
    private SavingsTransactionDao savingsTransactionDao;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private RecipientDao recipientDao;

    /**
     * Retrieves the list of primary account transactions for the given user.
     *
     * @param username the username of the user
     * @return list of primary account transactions
     */
    @Override
    public List<PrimaryTransaction> findPrimaryTransactionList(String username) {
        User user = userService.findByUsername(username);
        return user.getPrimaryAccount().getPrimaryTransactionList();
    }

    /**
     * Retrieves the list of savings account transactions for the given user.
     *
     * @param username the username of the user
     * @return list of savings account transactions
     */
    @Override
    public List<SavingsTransaction> findSavingsTransactionList(String username) {
        User user = userService.findByUsername(username);
        return user.getSavingsAccount().getSavingsTransactionList();
    }

    /**
     * Saves a primary account deposit transaction.
     *
     * @param primaryTransaction the transaction to save
     */
    @Override
    public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    /**
     * Saves a savings account deposit transaction.
     *
     * @param savingsTransaction the transaction to save
     */
    @Override
    public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }

    /**
     * Saves a primary account withdrawal transaction.
     *
     * @param primaryTransaction the transaction to save
     */
    @Override
    public void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    /**
     * Saves a savings account withdrawal transaction.
     *
     * @param savingsTransaction the transaction to save
     */
    @Override
    public void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }

    /**
     * Transfers funds between primary and savings accounts.
     *
     * @param transferFrom   source account type ("Primary" or "Savings")
     * @param transferTo     target account type ("Primary" or "Savings")
     * @param amount         the amount to transfer
     * @param primaryAccount the primary account object
     * @param savingsAccount the savings account object
     * @param principal      the authenticated user
     * @throws Exception if the transfer is invalid
     */
    @Override
    public void betweenAccountsTransfer(String transferFrom, String transferTo, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount, Principal principal) throws Exception {
        if (transferFrom.equalsIgnoreCase("Primary") && transferTo.equalsIgnoreCase("Savings")) {
            // Transfer from primary to savings account
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            // Record transaction in primary account
            Date date = new Date();
            PrimaryTransaction primaryTransaction = new PrimaryTransaction(
                    date,
                    "Between account transfer from " + transferFrom,
                    "Transfer",
                    "Finished",
                    Double.parseDouble(amount),
                    primaryAccount.getAccountBalance(),
                    primaryAccount
            );
            primaryTransactionDao.save(primaryTransaction);
        } else if (transferFrom.equalsIgnoreCase("Savings") && transferTo.equalsIgnoreCase("Primary")) {
            // Transfer from savings to primary account
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            // Record transaction in savings account
            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(
                    date,
                    "Between account transfer from " + transferFrom,
                    "Transfer",
                    "Finished",
                    Double.parseDouble(amount),
                    savingsAccount.getAccountBalance(),
                    savingsAccount
            );
            savingsTransactionDao.save(savingsTransaction);
        } else {
            throw new Exception("Invalid Transfer");
        }
    }

    /**
     * Retrieves the list of recipients for the authenticated user.
     *
     * @param principal the authenticated user
     * @return list of recipients
     */
    @Override
    public List<Recipient> findRecipientList(Principal principal) {
        String username = principal.getName();
        return recipientDao.findAll().stream()
                .filter(recipient -> username.equals(recipient.getUser().getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * Saves a recipient to the database.
     *
     * @param recipient the recipient to save
     * @return the saved recipient
     */
    @Override
    public Recipient saveRecipient(Recipient recipient) {
        return recipientDao.save(recipient);
    }

    /**
     * Finds a recipient by name.
     *
     * @param recipientName the name of the recipient
     * @return the recipient object
     */
    @Override
    public Recipient findRecipientByName(String recipientName) {
        return recipientDao.findByName(recipientName);
    }

    /**
     * Deletes a recipient by name.
     *
     * @param recipientName the name of the recipient
     */
    @Override
    public void deleteRecipientByName(String recipientName) {
        recipientDao.deleteByName(recipientName);
    }

    /**
     * Transfers funds to someone else using a recipient.
     *
     * @param recipient      the recipient of the transfer
     * @param accountType    source account type ("Primary" or "Savings")
     * @param amount         the amount to transfer
     * @param primaryAccount the primary account object
     * @param savingsAccount the savings account object
     */
    @Override
    public void toSomeoneElseTransfer(Recipient recipient, String accountType, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) {
        if (accountType.equalsIgnoreCase("Primary")) {
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            // Record transaction in primary account
            Date date = new Date();
            PrimaryTransaction primaryTransaction = new PrimaryTransaction(
                    date,
                    "Transfer to recipient " + recipient.getName(),
                    "Transfer",
                    "Finished",
                    Double.parseDouble(amount),
                    primaryAccount.getAccountBalance(),
                    primaryAccount
            );
            primaryTransactionDao.save(primaryTransaction);
        } else if (accountType.equalsIgnoreCase("Savings")) {
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            // Record transaction in savings account
            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(
                    date,
                    "Transfer to recipient " + recipient.getName(),
                    "Transfer",
                    "Finished",
                    Double.parseDouble(amount),
                    savingsAccount.getAccountBalance(),
                    savingsAccount
            );
            savingsTransactionDao.save(savingsTransaction);
        }
    }

    /**
     * Processes external payments using the card number.
     *
     * @param cardNumber the card number of the account
     * @param amount     the amount to deduct
     * @return true if the payment is successful, false otherwise
     */
    @Override
    public boolean processExternalPayment(int cardNumber, BigDecimal amount) {
        PrimaryAccount primaryAccount = primaryAccountDao.findByCardNumber(cardNumber);

        if (primaryAccount != null && primaryAccount.getAccountBalance().compareTo(amount) >= 0) {
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(amount));
            primaryAccountDao.save(primaryAccount);

            // Record transaction
            Date date = new Date();
            PrimaryTransaction transaction = new PrimaryTransaction(
                    date,
                    "External payment",
                    "Payment",
                    "Finished",
                    amount.doubleValue(),
                    primaryAccount.getAccountBalance(),
                    primaryAccount
            );
            primaryTransactionDao.save(transaction);

            return true;
        }
        return false;
    }
}
