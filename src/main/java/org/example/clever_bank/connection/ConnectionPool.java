package org.example.clever_bank.connection;

import org.example.clever_bank.exception.InitializeConnectionPoolError;

import java.sql.Connection;

public interface ConnectionPool {

    boolean init() throws InitializeConnectionPoolError;

    boolean shoutDown();

    Connection takeConnection();

    void returnConnection(Connection connection);

    static ConnectionPool lockingPool() {
        return LockingConnectionPool.getInstance();
    }
}
