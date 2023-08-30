package org.example.clever_bank.service;

import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Transaction;

import java.time.LocalDateTime;

public interface TransactionService extends Service<Transaction> {

    void getStatementOfAccount(Account account, LocalDateTime periodFrom, LocalDateTime periodTo);

}
