package org.example.clever_bank.dao.impl;

import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AbstractDao;
import org.example.clever_bank.dao.BankAccountDao;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.exception.DaoException;

import java.util.List;
import java.util.Optional;

public class BankAccountDaoImpl extends AbstractDao<BankAccount> implements BankAccountDao<BankAccount> {

    public BankAccountDaoImpl(ConnectionPool pool, Logger log) {
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
}
