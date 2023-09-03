package org.example.clever_bank.service;

import org.example.clever_bank.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface transaction service layer
 */
public interface TransactionService extends Service<Transaction> {

    /**
     * Create statement of customer account
     *
     * @param accountId - account id
     * @param periodFrom - start date
     * @param periodTo - finish date
     * @return statement of account text
     */
    String createStatementOfAccount(Long accountId, LocalDateTime periodFrom, LocalDateTime periodTo, String fileType);

}
