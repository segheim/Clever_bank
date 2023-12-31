package org.example.clever_bank.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.entity.Loggable;
import org.example.clever_bank.exception.InitializeConnectionPoolError;
import org.example.clever_bank.util.ConfigurationManager;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of connection pool
 */
public class LockingConnectionPool implements ConnectionPool {

    private static final Logger logger = LogManager.getLogger(LockingConnectionPool.class);

    private static final String DB_URL = ConfigurationManager.getProperty("db.url");
    private static final String DB_USER = ConfigurationManager.getProperty("db.user");
    private static final String DB_PASSWORD = ConfigurationManager.getProperty("db.password");
    public static final int DEFAULT_POOL_SIZE = Integer.parseInt(ConfigurationManager.getProperty("db.poolsize"));
    private static final String PATH_DATABASE_DRIVER = ConfigurationManager.getProperty("db.driver");

    private static AtomicBoolean isCreated = new AtomicBoolean();
    private static LockingConnectionPool instance;

    private final BlockingQueue<ProxyConnection> availableConnections;
    private final BlockingQueue<ProxyConnection> givenAwayConnections;

    private final AtomicBoolean initialize = new AtomicBoolean();

    private final static Lock locker = new ReentrantLock();

    public LockingConnectionPool() {
        this.availableConnections = new LinkedBlockingQueue<>();
        this.givenAwayConnections = new LinkedBlockingQueue<>();
    }

    @Loggable
    public static LockingConnectionPool getInstance() {
        if (!isCreated.get()) {
            locker.lock();
            try {
                if (instance == null) {
                    instance = new LockingConnectionPool();
                    isCreated.set(true);
                }
            } finally {
                locker.unlock();
            }
        }
        logger.info("create instance: {}", instance);
        return instance;
    }

    @Override
    public boolean init() {
        if (initialize.compareAndSet(false, true)) {
            try {
                Class.forName(PATH_DATABASE_DRIVER);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            registerDrivers();
            initializeConnections(DEFAULT_POOL_SIZE);
            return true;
        }
        return false;
    }

    @Override
    public boolean shoutDown() {
        if (initialize.compareAndSet(true, false)) {
            closeConnections();
            deregisterDrivers();
            return true;
        }
        return false;
    }

    private void closeConnections() {
        closeCollectionConnections(availableConnections);
        closeCollectionConnections(givenAwayConnections);
    }

    @Loggable
    private void closeCollectionConnections(BlockingQueue<ProxyConnection> collection) {
        try {
            collection.take().realClose();
        } catch (SQLException e) {
            logger.error("could not close connection", e);
        } catch (InterruptedException e) {
            logger.error("method closeCollectionConnections from LockingConnectionPool was interrupted", e);
        }
    }

    @Override
    @Loggable
    public Connection takeConnection() {
        ProxyConnection proxyConnection = null;
        try {
            proxyConnection = availableConnections.take();
            if (proxyConnection.isClosed()) {
                proxyConnection = new ProxyConnection(DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD));
            }
            givenAwayConnections.add(proxyConnection);
        } catch (InterruptedException e) {
            logger.error("method takeConnection from LockingConnectionPool was interrupted", e);
            Thread.currentThread().interrupt();
        } catch (SQLException e) {
            logger.error("could not close connection", e);
        }
        return proxyConnection;
    }

    @Override
    @Loggable
    public void returnConnection(Connection connection) {
        if (connection instanceof ProxyConnection && givenAwayConnections.remove(connection)) {
            try {
                availableConnections.put((ProxyConnection) connection);
            } catch (InterruptedException e) {
                logger.error("method takeConnection from LockingConnectionPool was interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Loggable
    private void initializeConnections(int amount) {
        try {
            for (int i = 0; i < amount; i++) {
                final Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                final ProxyConnection proxyConnection = new ProxyConnection(connection);
                availableConnections.add(proxyConnection);
            }
        } catch (SQLException e) {
            logger.fatal("error occurred creating Connection", e);
            throw new InitializeConnectionPoolError("failed creating Connection", e);
        }
    }

    @Loggable
    private void registerDrivers() {
        try {
            DriverManager.registerDriver(DriverManager.getDriver(DB_URL));
        } catch (SQLException e) {
            logger.info("could not register drivers", e);
            throw new InitializeConnectionPoolError("sql drivers are not initialize", e);
        }
    }

    @Loggable
    private void deregisterDrivers() {
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            try {
                DriverManager.deregisterDriver(drivers.nextElement());
            } catch (SQLException e) {
                logger.error("could not deregister drivers", e);
            }
        }
    }
}
