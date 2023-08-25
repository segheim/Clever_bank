package org.example.clever_bank.dao;

import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.entity.Entity;

import java.util.List;
import java.util.Optional;

public class BankDaoImpl extends AbstractDao implements BankDao{

    protected BankDaoImpl(ConnectionPool pool, Logger log) {
        super(pool, log);
    }

    @Override
    public Optional create(Entity entity) {
        return Optional.empty();
    }

    @Override
    public Optional read(Long id) {
        return Optional.empty();
    }

    @Override
    public List readAll() {
        return null;
    }

    @Override
    public Optional update(Entity entity) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
