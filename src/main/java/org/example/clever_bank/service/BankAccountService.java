package org.example.clever_bank.service;

import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;

import java.math.BigDecimal;

public interface BankAccountService extends Service<BankAccount> {

    BankAccount replenishmentAccount(Long id, BigDecimal amount);

    BankAccount withdrawal(Long id, BigDecimal moneyAmount);


    BigDecimal internalTransfer(Long ownerId, String loginUser, BigDecimal amount);

    BigDecimal externalTransfer(Long ownerId, Long bankId, String loginUser, BigDecimal amount);

    static BankAccountService getInstance() {
        return BankAccountService.getInstance();
    }
}
