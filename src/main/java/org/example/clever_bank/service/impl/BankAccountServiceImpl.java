package org.example.clever_bank.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AccountDao;
import org.example.clever_bank.dao.BankAccountDao;
import org.example.clever_bank.dao.BankDao;
import org.example.clever_bank.dao.TransactionDao;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.service.BankAccountService;
import org.example.clever_bank.service.text.PaperWorker;
import org.example.clever_bank.util.ConfigurationManager;
import org.example.clever_bank.validation.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BankAccountServiceImpl implements BankAccountService {

    private static final Logger logger = LogManager.getLogger(BankAccountServiceImpl.class);

    public static final Long CLEVER_BANK_ID = 1L;
    public static final String CLEVER_BANK_NAME = "clever_bank";

    private final BankAccountDao bankAccountDao;
    private final TransactionDao transactionDao;
    private final AccountDao accountDao;
    private final BankDao bankDao;
    private final PaperWorker paperWorker;

    public BankAccountServiceImpl(BankAccountDao bankAccountDao, TransactionDao transactionDao, AccountDao accountDao, BankDao bankDao, PaperWorker paperWorker) {
        this.bankAccountDao = bankAccountDao;
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.bankDao = bankDao;
        this.paperWorker = paperWorker;
    }

    @Override
    public BankAccount add(BankAccount bankAccount) {
        if (!Validator.getInstance().validateAmount(bankAccount.getBalance())) {
            throw new ServiceException("Enter correct amount of money");
        }
        accountDao.read(bankAccount.getAccount().getId())
                .orElseThrow(() -> new NotFoundEntityException(String.format("Account with id=%d is not present", bankAccount.getAccount().getId())));
        BankAccount createBankAccount;
        Connection connection = ConnectionPool.lockingPool().takeConnection();
        try {
            connection.setAutoCommit(false);
            createBankAccount = bankAccountDao.create(bankAccount)
                    .orElseThrow(() -> new ServiceException("Bank account is not created"));
            List<Bank> banks = bankAccount.getBanks();
            for (Bank bank : banks) {
                boolean bankBankAccount = bankAccountDao.createBankBankAccount(bank.getId(), createBankAccount.getId());
                if (!bankBankAccount) {
                    connection.rollback();
                }
            }
            connection.commit();
        } catch (SQLException | NotFoundEntityException | ServiceException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Database access error occurs connection rollback", ex);
                throw new ServiceException("Database access error occurs connection rollback");
            }
            throw new ServiceException(String.format("could not create bank account. %s", e.getMessage()));
        } finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                logger.error("Database access error occurs connection close", e);
            }
        }
        return createBankAccount;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public BankAccount findById(Long id) {
        return bankAccountDao.read(id).orElseThrow(() -> new ServiceException("Bank account is not found"));
    }

    /**
     * @return
     */
    @Override
    public List<BankAccount> findAll() {
        List<BankAccount> bankAccounts = bankAccountDao.readAll();
        if (bankAccounts.isEmpty()) {
            throw new ServiceException("Empty");
        }
        return bankAccounts;
    }

    @Override
    public BankAccount update(BankAccount bankAccount) {
        if (Validator.getInstance().validateAmount(bankAccount.getBalance())) {
            throw new ServiceException("Enter correct amount of money");
        }
        return bankAccountDao.update(bankAccount).orElseThrow(() -> new ServiceException("Bank account is not updated"));
    }

    @Override
    public boolean remove(Long id) {
        return bankAccountDao.delete(id);
    }

    @Override
    public BankAccount replenishmentAccount(Long accountId, BigDecimal amount) {
        BankAccount updatedBankAccount;
        Connection connection = ConnectionPool.lockingPool().takeConnection();
        try {
            connection.setAutoCommit(false);
            BankAccount bankAccount = bankAccountDao.readByAccountIdAndBankId(accountId, CLEVER_BANK_ID)
                    .orElseThrow(() -> new NotFoundEntityException("Not found bank account"));
            BigDecimal balance = bankAccount.getBalance();
            bankAccount.setBalance(balance.add(amount));
            updatedBankAccount = bankAccountDao.update(bankAccount)
                    .orElseThrow(() -> new ServiceException("Operation is failed. Bank account is not updated"));

            Transaction transaction = craeteNewTransaction(amount, bankAccount, bankAccount, ConfigurationManager.getProperty("operation.replenishment"));
            paperWorker.createBill(transaction.getId(), transaction.getType(), CLEVER_BANK_NAME, CLEVER_BANK_NAME,
                    bankAccount.getId(), bankAccount.getId(), amount, transaction.getDateCreate());

            connection.commit();
        } catch (SQLException | NotFoundEntityException | ServiceException | IOException | URISyntaxException e) {
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
        return updatedBankAccount;
    }

    @Override
    public BankAccount withdrawal(Long accountId, BigDecimal amount) {
        BankAccount updatedBankAccount;
        Connection connection = ConnectionPool.lockingPool().takeConnection();
        try {
            connection.setAutoCommit(false);
            BankAccount bankAccount = bankAccountDao.readByAccountIdAndBankId(accountId, CLEVER_BANK_ID)
                    .orElseThrow(() -> new NotFoundEntityException("Not found bank account"));
            BigDecimal balance = bankAccount.getBalance();
            BigDecimal result = balance.subtract(amount);
            if (result.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("Not enough funding");
            }
            bankAccount.setBalance(result);
            updatedBankAccount = bankAccountDao.update(bankAccount)
                    .orElseThrow(() -> new ServiceException("Operation is failed.Bank account is not updated"));

            Transaction transaction = craeteNewTransaction(amount, bankAccount, bankAccount, ConfigurationManager.getProperty("operation.withdrawal"));
            paperWorker.createBill(transaction.getId(), transaction.getType(), CLEVER_BANK_NAME, CLEVER_BANK_NAME,
                    bankAccount.getId(), bankAccount.getId(), amount, transaction.getDateCreate());

            connection.commit();
        } catch (SQLException | NotFoundEntityException | ServiceException | IOException | URISyntaxException e) {
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

        return updatedBankAccount;
    }

    @Override
    public BigDecimal transferMoney(Long ownerId, Long bankId, String loginUser, BigDecimal amount) {
        BankAccount owner;
        Connection connection = ConnectionPool.lockingPool().takeConnection();
        try {
            connection.setAutoCommit(false);
            BankAccount bankAccountOwner = bankAccountDao.readByAccountIdAndBankId(ownerId, CLEVER_BANK_ID)
                    .orElseThrow(() -> new NotFoundEntityException("Bank Account owner is not found"));

            BankAccount bankAccountUser = bankAccountDao.readByAccountLoginAndBankId(loginUser, bankId)
                    .orElseThrow(() -> new NotFoundEntityException("Bank Account of user is not found"));

            Transaction transaction = craeteNewTransaction(amount, bankAccountOwner, bankAccountUser, ConfigurationManager.getProperty("operation.transaction"));

            BigDecimal ownerBalance = bankAccountOwner.getBalance();
            BigDecimal newOwnerBalance = ownerBalance.subtract(amount);
            if (newOwnerBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("Not enough funding");
            }
            bankAccountOwner.setBalance(newOwnerBalance);
            Optional<BankAccount> optionalOwnerBankAccountUpdated = bankAccountDao.update(bankAccountOwner);

            BigDecimal userBalance = bankAccountUser.getBalance();
            bankAccountUser.setBalance(userBalance.add(amount));
            Optional<BankAccount> optionalUserBankAccountUpdated = bankAccountDao.update(bankAccountUser);

            if (optionalOwnerBankAccountUpdated.isEmpty() || optionalUserBankAccountUpdated.isEmpty()) {
                connection.rollback();
            }
            owner = optionalOwnerBankAccountUpdated.get();

            Bank bankTo = bankDao.read(bankId).orElseThrow(() -> new NotFoundEntityException("Bank is not found"));

            paperWorker.createBill(transaction.getId(), transaction.getType(), CLEVER_BANK_NAME, bankTo.getName(),
                    bankAccountOwner.getId(), bankAccountUser.getId(), amount, transaction.getDateCreate());

            connection.commit();
        } catch (SQLException | NotFoundEntityException | ServiceException | IOException | URISyntaxException e) {
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
        return owner.getBalance();

    }

    @Override
    public void accruePercentOnUserBalancesOfCleverBank() {
        List<BankAccount> bankAccounts = bankAccountDao.readByBankId(CLEVER_BANK_ID);
        if (bankAccounts.isEmpty()) {
            throw new ServiceException("Empty");
        }
        bankAccounts.stream()
                .peek(bankAccount -> bankAccount.setBalance(
                        bankAccount.getBalance().add(bankAccount.getBalance().multiply(BigDecimal.valueOf(Double
                                .valueOf(ConfigurationManager.getProperty("accrual.percentage")))))
                ))
                .collect(Collectors.toList());
        bankAccounts.stream()
                .forEach(bankAccount -> bankAccountDao.update(bankAccount)
                        .orElseThrow(() -> new NotFoundEntityException(String.format("Bank account with id=%d is not updated", bankAccount.getId()))));
    }

    private Transaction craeteNewTransaction(BigDecimal amount, BankAccount bankAccountOwner, BankAccount bankAccountUser, String type) {
        Transaction transaction = Transaction.builder()
                .bankAccountFrom(bankAccountOwner)
                .bankAccountTo(bankAccountUser)
                .sum(amount)
                .type(type)
                .build();

        Transaction createTransaction = transactionDao.create(transaction)
                .orElseThrow(() -> new ServiceException("Transaction is not created"));
        return transactionDao.read(createTransaction.getId())
                .orElseThrow(() -> new ServiceException("Transaction is not read"));
    }
}

