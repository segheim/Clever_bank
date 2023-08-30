package org.example.clever_bank.service.impl;

import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.dao.impl.TransactionDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.TransactionService;
import org.example.clever_bank.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionDaoImpl transactionDao;
    private final BankAccountDaoImpl bankAccountDao;

    private TransactionServiceImpl(TransactionDaoImpl transactionDao, BankAccountDaoImpl bankAccountDao) {
        this.transactionDao = transactionDao;
        this.bankAccountDao = bankAccountDao;
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
    public void getStatementOfAccount(Account account, LocalDateTime periodFrom, LocalDateTime periodTo) {

    }

    public static TransactionServiceImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final TransactionServiceImpl INSTANCE = new TransactionServiceImpl(
                TransactionDaoImpl.getInstance(), BankAccountDaoImpl.getInstance()
                );
    }
}
