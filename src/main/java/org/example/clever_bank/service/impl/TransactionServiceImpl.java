package org.example.clever_bank.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.dao.impl.TransactionDaoImpl;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.TransactionService;
import org.example.clever_bank.service.text.PaperWorker;
import org.example.clever_bank.util.ConfigurationManager;
import org.example.clever_bank.validation.Validator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LogManager.getLogger(TransactionServiceImpl.class);

    private final TransactionDaoImpl transactionDao;
    private final BankAccountDaoImpl bankAccountDao;
    private final AccountDaoImpl accountDao;
    private final PaperWorker paperWorker;

    public TransactionServiceImpl(TransactionDaoImpl transactionDao, BankAccountDaoImpl bankAccountDao, AccountDaoImpl accountDao, PaperWorker paperWorker) {
        this.transactionDao = transactionDao;
        this.bankAccountDao = bankAccountDao;
        this.accountDao = accountDao;
        this.paperWorker = paperWorker;
    }

    @Override
    public Transaction add(Transaction transaction) throws ValidationException {
        if (!Validator.getInstance().validateType(transaction.getType())) {
            throw new ValidationException("Transaction type is not valid");
        }
        bankAccountDao.read(transaction.getBankAccountFrom().getId())
                .orElseThrow(() -> new NotFoundEntityException(String.format("Bank account with id=%d is not present", transaction.getId())));
        bankAccountDao.read(transaction.getBankAccountTo().getId())
                .orElseThrow(() -> new NotFoundEntityException(String.format("Bank account with id=%d is not present", transaction.getId())));
        return transactionDao.create(transaction)
                .orElseThrow(() -> new ServiceException("Transaction is not created"));
    }

    @Override
    public Transaction findById(Long id) {
        return transactionDao.read(id)
                .orElseThrow(() -> new NotFoundEntityException(String.format("Transaction with id=%d is not found", id)));
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = transactionDao.readAll();
        if (transactions.isEmpty()) {
            throw new ServiceException("Empty");
        }
        return transactions;
    }

    @Override
    public Transaction update(Transaction transaction) throws ValidationException {
        return transactionDao.update(transaction)
                .orElseThrow(() -> new ServiceException("Transaction is not updated"));
    }

    @Override
    public boolean remove(Long id) {
        boolean flag;
        try {
            flag = transactionDao.delete(id);
        } catch (Exception e) {
            throw new ServiceException("Transaction is not deleted", e);
        }
        return flag;
    }

    @Override
    public String createStatementOfAccount(Long accountId, LocalDateTime periodFrom, LocalDateTime periodTo) {
        if (periodFrom.equals(periodTo)) {
            BankAccount bankAccount = bankAccountDao.readByAccountIdAndBankId(accountId, Long.valueOf(ConfigurationManager.getProperty("bank.id")))
                    .orElseThrow(() -> new NotFoundEntityException(String.format("Bank account with account id=%d is not found", accountId)));
            periodFrom = bankAccount.getDateCreate();
        }
        String statementOfAccount;
        Connection connection = ConnectionPool.lockingPool().takeConnection();
        try {
            connection.setAutoCommit(false);
            accountDao.read(accountId).orElseThrow(() -> new NotFoundEntityException("Account is not found"));
            List<Transaction> transactions = transactionDao.readByPeriodAndAccountId(accountId, periodFrom, periodTo);
            if (transactions.isEmpty()) {
                throw new ServiceException("Empty");
            }
            statementOfAccount = paperWorker.createStatement(transactions, periodFrom, periodTo);
            connection.commit();
        } catch (SQLException | NotFoundEntityException | IOException | URISyntaxException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Database access error occurs connection rollback", ex);
                throw new ServiceException("Database access error occurs connection rollback");
            }
            throw new ServiceException(String.format("could not transfer money. %s", e.getMessage()));
        } finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                logger.error("Database access error occurs connection close", e);
            }
        }
        return statementOfAccount;
    }
}
