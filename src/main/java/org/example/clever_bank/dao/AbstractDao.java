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
}
