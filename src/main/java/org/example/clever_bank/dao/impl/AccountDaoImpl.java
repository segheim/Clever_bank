package org.example.clever_bank.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AbstractDao;
import org.example.clever_bank.dao.AccountDao;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.DaoException;
import org.example.clever_bank.util.ConfigurationManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {

    private static final Logger logger = LogManager.getLogger(AccountDaoImpl.class);

    public static final String INSERT_NEW_ACCOUNT = "insert into accounts (login, password) values (?,?)";
    public static final String SELECT_ACCOUNT_BY_ID = "select id as id, login as login, password as password from accounts where id=?";
    public static final String SELECT_ACCOUNT_BY_LOGIN = "select id as id, login as login, password as password from accounts where login=?";
    public static final String SELECT_ALL_ACCOUNTS = "select id as id, login as login, password as password from accounts";
    public static final String UPDATE_ACCOUNT = "update accounts set login=?, password=? where id=?";
    public static final String DELETE_ACCOUNT = "delete from accounts where id=?";

    private AccountDaoImpl(ConnectionPool pool, Logger log) {
        super(pool, log);
    }

    @Override
    public Optional<Account> create(Account entity) throws DaoException {
        logger.trace("start create account");
        Optional<Account> createdAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getLogin());
            preparedStatement.setString(2, entity.getPassword());
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines != 0) {
                final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long key = generatedKeys.getLong(1);
                    createdAccount = Optional.of(new Account(key, entity.getLogin(), entity.getPassword()));
                }
            }
        } catch (SQLException e) {
            logger.error("sql error, could not create account", e);
            throw new DaoException("Account not create", e);
        }
        return createdAccount;
    }

    @Override
    public Optional<Account> read(Long id) throws DaoException {
        logger.trace("start read account");
        Optional<Account> readAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID)) {
            preparedStatement.setLong(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final Account account = executeAccount(resultSet);
                return Optional.of(account);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not find account", e);
            throw new DaoException("Account is not read", e);
        }
        return readAccount;
    }

    @Override
    public Optional<Account> readByLogin(String login) {
        logger.trace("start read by login account");
        Optional<Account> readAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_LOGIN)) {
            preparedStatement.setString(1, login);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final Account account = executeAccount(resultSet);
                return Optional.of(account);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not find account", e);
            throw new DaoException("Account is not read", e);
        }
        return readAccount;
    }

    @Override
    public List<Account> readAll() {
        logger.trace("start read all account");
        List<Account> accounts = new ArrayList<>();
        try (final Connection connection = pool.takeConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(SELECT_ALL_ACCOUNTS)) {
            while (resultSet.next()) {
                final Account account = executeAccount(resultSet);
                accounts.add(account);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not found accounts", e);
            throw new DaoException("Account is not read", e);
        }
        return accounts;
    }

    @Override
    public Optional<Account> update(Account entity) {
        logger.trace("start update account");
        Optional<Account> updatedAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ACCOUNT)) {
            preparedStatement.setString(1, entity.getLogin());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setLong(3, entity.getId());
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines > 0) {
                updatedAccount = Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not update account", e);
            throw new DaoException("Account is not updated", e);
        }
        return updatedAccount;
    }

    @Override
    public boolean delete(Long id) {
        logger.trace("start delete account");
        boolean deleteAccount = false;
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ACCOUNT)) {
            preparedStatement.setLong(1, id);
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines != 0) {
                deleteAccount = true;
            }
        } catch (SQLException e) {
            logger.error("sql error, could not delete account", e);
            throw new DaoException("Account is not deleted", e);
        }
        return deleteAccount;
    }

    private Account executeAccount(ResultSet resultSet) throws SQLException {
        return new Account(resultSet.getLong(ConfigurationManager.getProperty("table.id")),
                resultSet.getString(ConfigurationManager.getProperty("table.login")),
                resultSet.getString(ConfigurationManager.getProperty("table.pass")));
    }

    public static AccountDaoImpl getInstance() {
        return Holder.INSTANCE;
    }


    private static class Holder {
        public static final AccountDaoImpl INSTANCE = new AccountDaoImpl(ConnectionPool.lockingPool(), logger);
    }
}
