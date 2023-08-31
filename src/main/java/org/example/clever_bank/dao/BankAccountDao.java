package org.example.clever_bank.dao;

import org.example.clever_bank.entity.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankAccountDao extends DaoBase<BankAccount> {

    Optional<BankAccount> readByAccountIdAndBankId(Long id, Long bankId);

    Optional<BankAccount> readByAccountLoginAndBankId(String login, Long bankId);

    List<BankAccount> readByBankId(Long bankId);

    boolean createBankBankAccount(Long bankId, Long bankAccountId);

}
