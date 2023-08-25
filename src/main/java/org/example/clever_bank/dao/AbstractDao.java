package org.example.clever_bank.dao;

import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.entity.Entity;
import org.example.clever_bank.exception.DaoException;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T extends Entity> {

    protected final ConnectionPool pool;
    private final Logger log;

    public AbstractDao(ConnectionPool pool, Logger log) {
        this.pool = pool;
        this.log = log;
    }

    public abstract Optional<T> create(T entity) throws DaoException;

    public abstract Optional<T> read(Long id) throws DaoException;

    public abstract List<T> readAll();

    public abstract Optional<T> update(T entity);

    public abstract boolean delete(Long id);
}
