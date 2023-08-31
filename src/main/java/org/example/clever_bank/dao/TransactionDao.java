package org.example.clever_bank.dao;

import org.example.clever_bank.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionDao extends DaoBase<Transaction> {

    List<Transaction> readByPeriodAndAccountId(Long accountId, LocalDateTime dateFrom, LocalDateTime dateTo);

}
