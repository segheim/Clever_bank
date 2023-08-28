package org.example.clever_bank.dao;

import org.example.clever_bank.entity.BankAccount;

import java.util.Optional;

public interface BankAccountDao{

    Optional<BankAccount> findByAccountIdAndBankId(Long id, Long bankId);

    Optional<BankAccount> findByAccountLoginAndBankId(String login, Long bankId);

}
