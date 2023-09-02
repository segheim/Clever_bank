package org.example.clever_bank.connection;

import org.example.clever_bank.exception.InitializeConnectionPoolError;

import java.sql.Connection;

/**
 * The interface Connection pool
 */
public interface ConnectionPool {

    /**
     * Initialize connection pool
     *
     * @return boolean
     * @throws InitializeConnectionPoolError
     */
    boolean init() throws InitializeConnectionPoolError;

    /**
     * Shout down connection pool
     *
     * @return boolean
     */
    boolean shoutDown();

    /**
     * Get connection from connection pool
     *
     * @return connection
     */
    Connection takeConnection();

    /**
     * Put back connection to connection pool
     *
     * @param connection - connection
     */
    void returnConnection(Connection connection);

    /**
     * Initialize connection pool by LockingPool
     * @return connection pool
     */
    static ConnectionPool lockingPool() {
        return LockingConnectionPool.getInstance();
    }
}
