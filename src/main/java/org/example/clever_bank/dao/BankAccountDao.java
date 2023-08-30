package org.example.clever_bank.dao;

import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;

import java.util.Optional;

public interface BankAccountDao{

    Optional<BankAccount> readByAccountIdAndBankId(Long id, Long bankId);

    Optional<BankAccount> readByAccountLoginAndBankId(String login, Long bankId);

    boolean createBankBankAccount(Long bankId, Long bankAccountId);

}
