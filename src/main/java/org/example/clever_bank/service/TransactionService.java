package org.example.clever_bank.service;

import org.example.clever_bank.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService extends Service<Transaction> {

    String createStatementOfAccount(Long accountId, LocalDateTime periodFrom, LocalDateTime periodTo);

}
