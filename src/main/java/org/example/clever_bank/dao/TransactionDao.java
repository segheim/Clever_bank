package org.example.clever_bank.dao;

import org.example.clever_bank.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Transaction dao layer
 */
public interface TransactionDao extends DaoBase<Transaction> {

    /**
     * Search transactions in database by period and id account
     *
     * @param accountId - account id
     * @param dateFrom - start date
     * @param dateTo - finish date
     * @return
     */
    List<Transaction> readByPeriodAndAccountId(Long accountId, LocalDateTime dateFrom, LocalDateTime dateTo);

}
