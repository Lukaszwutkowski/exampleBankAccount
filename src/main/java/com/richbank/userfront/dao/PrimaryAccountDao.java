package com.richbank.userfront.dao;

import com.richbank.userfront.domain.PrimaryAccount;
import org.springframework.data.repository.CrudRepository;

public interface PrimaryAccountDao extends CrudRepository<PrimaryAccount, Long> {

    PrimaryAccount findByAccountNumber(int accountNumber);
    PrimaryAccount findByCardNumber(int cardNumber);

}
