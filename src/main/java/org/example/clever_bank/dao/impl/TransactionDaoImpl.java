package org.example.clever_bank.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AbstractDao;
import org.example.clever_bank.dao.TransactionDao;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.util.ConfigurationManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TransactionDaoImpl extends AbstractDao<Transaction> implements TransactionDao {

    private static final Logger logger = LogManager.getLogger(TransactionDaoImpl.class);

    public static final String INSERT_NEW_TRANSACTION = "insert into transactions (bank_account_id_from, bank_account_id_to, sum) values (?, ?, ?)";
    public static final String SELECT_ALL_TRANSACTIONS = "select t.id as id, t.sum as sum, t.date_create as date_create, ba1.id as owner_ba_id, ba1.balance as owner_ba_balance," +
            " ba2.id as user_ba_id, ba2.balance as user_ba_balance, a1.id as owner_account_id, a1.login as owner_account_login," +
            " a2.id as user_account_id, a2.login as user_account_login from transactions t join bank_accounts ba1" +
            " on ba1.id=t.bank_account_id_from join bank_accounts ba2 on ba2.id=t.bank_account_id_to join accounts a1" +
            " on ba1.account_id = a1.id join accounts a2 on ba2.account_id = a2.id";
    public static final String SELECT_TRANSACTION_BY_ID = "select t.id as id, t.sum as sum, t.date_create as date_create, ba1.id as owner_ba_id, ba1.balance as owner_ba_balance," +
            " ba2.id as user_ba_id, ba2.balance as user_ba_balance, a1.id as owner_account_id, a1.login as owner_account_login," +
            " a2.id as user_account_id, a2.login as user_account_login from transactions t join bank_accounts ba1" +
            " on ba1.id=t.bank_account_id_from join bank_accounts ba2 on ba2.id=t.bank_account_id_to join accounts a1" +
            " on ba1.account_id = a1.id join accounts a2 on ba2.account_id = a2.id where t.id=?";

    private TransactionDaoImpl(ConnectionPool pool, Logger log) {
        super(pool, log);
    }

    @Override
    public Optional<Transaction> create(Transaction entity) {
        logger.trace("start create transaction");
        Optional<Transaction> createdTransaction = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_TRANSACTION, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, entity.getBankAccountFrom().getId());
            preparedStatement.setLong(2, entity.getBankAccountTo().getId());
            preparedStatement.setBigDecimal(3, entity.getSum());
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines != 0) {
                final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long key = generatedKeys.getLong(1);
                    entity.setId(key);
                    createdTransaction = Optional.of(entity);
                }
            }
        } catch (SQLException e) {
            logger.error("sql error, could not create transaction", e);
        }
        return createdTransaction;
    }

    @Override
    public Optional<Transaction> read(Long id) {
        logger.trace("start read transaction");
        Optional<Transaction> readTransaction = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TRANSACTION_BY_ID)) {
            preparedStatement.setLong(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Transaction transaction = executeTransaction(resultSet);
                return Optional.of(transaction);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not find transaction", e);
        }
        return readTransaction;
    }

    @Override
    public List<Transaction> readAll() {
        logger.trace("start read all transactions");
        List<Transaction> transactions = new ArrayList<>();
        try (final Connection connection = pool.takeConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(SELECT_ALL_TRANSACTIONS)) {
            while (resultSet.next()) {
                Transaction transaction = executeTransaction(resultSet);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not found transactions", e);
            return Collections.emptyList();
        }
        return transactions;
    }

    @Override
    public Optional<Transaction> update(Transaction entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Long id) {
        throw new UnsupportedOperationException();
    }

    private Transaction executeTransaction(ResultSet resultSet) throws SQLException {
        return Transaction.builder()
                .id(resultSet.getLong(ConfigurationManager.getProperty("table.id")))
                .bankAccountFrom(BankAccount.builder()
                        .id(resultSet.getLong(ConfigurationManager.getProperty("table.owner_ba_id")))
                        .account(Account.builder()
                                .id(resultSet.getLong(ConfigurationManager.getProperty("table.owner_account_id")))
                                .login(resultSet.getString(ConfigurationManager.getProperty("table.owner_account_login")))
                                .build())
                        .balance(resultSet.getBigDecimal(ConfigurationManager.getProperty("table.owner_ba_balance")))
                        .build())
                .bankAccountTo(BankAccount.builder()
                        .id(resultSet.getLong(ConfigurationManager.getProperty("table.user_ba_id")))
                        .account(Account.builder()
                                .id(resultSet.getLong(ConfigurationManager.getProperty("table.user_account_id")))
                                .login(resultSet.getString(ConfigurationManager.getProperty("table.user_account_login")))
                                .build())
                        .balance(resultSet.getBigDecimal(ConfigurationManager.getProperty("table.user_ba_balance")))
                        .build())
                .sum(resultSet.getBigDecimal(ConfigurationManager.getProperty("table.sum")))
                .dateCreate(resultSet.getTimestamp(ConfigurationManager.getProperty("table.date_create")).toLocalDateTime())
                .build();
    }

    public static TransactionDaoImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final TransactionDaoImpl INSTANCE = new TransactionDaoImpl(ConnectionPool.lockingPool(), logger);
    }
}
