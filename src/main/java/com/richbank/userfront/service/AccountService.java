package com.richbank.userfront.service;

import com.richbank.userfront.domain.PrimaryAccount;
import com.richbank.userfront.domain.SavingsAccount;

public interface AccountService {
    PrimaryAccount createPrimaryAccount();
    SavingsAccount createSavingsAccount();
}
