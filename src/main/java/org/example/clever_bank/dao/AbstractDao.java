package org.example.clever_bank.dao;

import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.entity.Entity;
import org.example.clever_bank.exception.DaoException;

import java.util.List;
import java.util.Optional;

/**
 * The abstract class dao layer
 * @param <T> - T
 */
public abstract class AbstractDao<T extends Entity> {

    protected final ConnectionPool pool;
    private final Logger log;

    /** Constructor of abstract class - initialise parameters ConnectionPool and logger
     * @param pool - connection pool
     * @param log - logger
     */
    public AbstractDao(ConnectionPool pool, Logger log) {
        this.pool = pool;
        this.log = log;
    }
}
