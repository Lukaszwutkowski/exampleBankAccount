package com.richbank.userfront.service.UserServiceImpl;


import com.richbank.userfront.dao.PrimaryTransactionDao;
import com.richbank.userfront.dao.SavingsTransactionDao;
import com.richbank.userfront.domain.PrimaryTransaction;
import com.richbank.userfront.domain.SavingsTransaction;
import com.richbank.userfront.domain.User;
import com.richbank.userfront.service.TransactionService;
import com.richbank.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private PrimaryTransactionDao primaryTransactionDao;

    @Autowired
    private SavingsTransactionDao savingsTransactionDao;

    @Override
    public List<PrimaryTransaction> findPrimaryTransactionList(String username) {
        User user = userService.findByUsername(username);
        List<PrimaryTransaction> primaryTransactionList = user.getPrimaryAccount().getPrimaryTransactionList();

        return primaryTransactionList;
    }

    @Override
    public List<SavingsTransaction> findSavingsTransactionList(String username) {
        User user = userService.findByUsername(username);
        List<SavingsTransaction> savingsTransactionList = user.getSavingsAccount().getSavingsTransactionList();

        return savingsTransactionList;
    }

    @Override
    public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    @Override
    public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }
}
