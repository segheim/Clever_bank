package org.example.clever_bank.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AbstractDao;
import org.example.clever_bank.dao.BankAccountDao;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.exception.DaoException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BankAccountDaoImpl extends AbstractDao<BankAccount> implements BankAccountDao {

    private static final Logger logger = LogManager.getLogger(BankAccountDaoImpl.class);

    private BankAccountDaoImpl(ConnectionPool pool, Logger log) {
        super(pool, log);
    }

    @Override
    public Optional<BankAccount> create(BankAccount entity) throws DaoException {
        return Optional.empty();
    }

    @Override
    public Optional<BankAccount> read(Long id) throws DaoException {
        return Optional.empty();
    }

    @Override
    public List<BankAccount> readAll() {
        return null;
    }

    @Override
    public Optional<BankAccount> update(BankAccount entity) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Optional<BankAccount> findByAccountIdAndBankId(Long id, Long bankId) {
        return Optional.of(BankAccount.builder()
                .id(1L)
                .balance(BigDecimal.TEN)
                .build());
    }

    @Override
    public Optional<BankAccount> findByAccountLoginAndBankId(String login, Long bankId) {
        return Optional.of(BankAccount.builder()
                .id(1L)
                .balance(BigDecimal.TEN)
                .build());
    }

    public static BankAccountDaoImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final BankAccountDaoImpl INSTANCE = new BankAccountDaoImpl(ConnectionPool.lockingPool(), logger);
    }
}
