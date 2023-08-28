package org.example.clever_bank.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.service.BankAccountService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BankAccountServiceImpl implements BankAccountService {

    private static final Logger logger = LogManager.getLogger(BankAccountServiceImpl.class);

    public static final Long CLEVER_BANK_ID = 1L;

    private final BankAccountDaoImpl bankAccountDao;

    public BankAccountServiceImpl(BankAccountDaoImpl bankAccountDaoImpl) {
        this.bankAccountDao = bankAccountDaoImpl;
    }

    @Override
    public boolean add(BankAccount bankAccount) {
        return false;
    }

    @Override
    public BankAccount findById(Long id) {
        return null;
    }

    @Override
    public List<BankAccount> findAll() {
        return null;
    }

    @Override
    public boolean update(BankAccount bankAccount) {
        return false;
    }

    @Override
    public boolean remove(Long id) {
        return false;
    }

    @Override
    public BankAccount replenishmentAccount(Long id, BigDecimal amount){
        BankAccount bankAccount = bankAccountDao.findByAccountIdAndBankId(id, CLEVER_BANK_ID).orElseThrow(() -> new NotFoundEntityException("Not found bank account"));
        BigDecimal balance = bankAccount.getBalance();
        bankAccount.setBalance(balance.add(amount));
        BankAccount updatedBankAccount = bankAccountDao.update(bankAccount).orElseThrow(() -> new ServiceException("Operation is failed"));
        return updatedBankAccount;
    }

    @Override
    public BankAccount withdrawal(Long id, BigDecimal moneyAmount) {
        BankAccount bankAccount = bankAccountDao.findByAccountIdAndBankId(id, CLEVER_BANK_ID).orElseThrow(() -> new NotFoundEntityException("Not found bank account"));
        BigDecimal balance = bankAccount.getBalance();
        BigDecimal result = balance.subtract(moneyAmount);
        if (result.compareTo(BigDecimal.ZERO) > 0) {
            throw new ServiceException("Not enough funding");
        }
        bankAccount.setBalance(result);
        BankAccount updatedBankAccount = bankAccountDao.update(bankAccount).orElseThrow(() -> new ServiceException("Operation is failed"));
        return updatedBankAccount;
    }


    @Override
    public BigDecimal externalTransfer(Long ownerId, Long bankId, String loginUser, BigDecimal amount) {
        BankAccount owner;
        Connection connection = ConnectionPool.lockingPool().takeConnection();
        try {
            connection.setAutoCommit(false);
            BankAccount bankAccountOwner = bankAccountDao.findByAccountIdAndBankId(ownerId, bankId).orElseThrow(() -> new NotFoundEntityException("Account owner is not found"));

            BankAccount bankAccountUser = bankAccountDao.findByAccountLoginAndBankId(loginUser, bankId).orElseThrow(() -> new NotFoundEntityException("Account of user is not found"));

            BigDecimal ownerBalance = bankAccountOwner.getBalance();
            BigDecimal newOwnerBalance = ownerBalance.subtract(amount);
            if (newOwnerBalance.compareTo(BigDecimal.ZERO) > 0) {
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
            connection.commit();
        } catch (SQLException e) {
            logger.error("sql error, database access error occurs(setAutoCommit)", e);
            throw new ServiceException("sql error, database access error occurs(setAutoCommit)");
        } catch (NotFoundEntityException | ServiceException e) {
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
                throw new ServiceException("Database access error occurs connection close");
            }
        }
        return owner.getBalance();
    }
    }


    public static BankAccountServiceImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final BankAccountServiceImpl INSTANCE = new BankAccountServiceImpl(BankAccountDaoImpl.getInstance());
    }

}
