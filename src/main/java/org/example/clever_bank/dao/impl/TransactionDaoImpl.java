package org.example.clever_bank.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AbstractDao;
import org.example.clever_bank.dao.TransactionDao;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.exception.DaoException;
import org.example.clever_bank.util.ConfigurationManager;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class TransactionDaoImpl extends AbstractDao<Transaction> implements TransactionDao<Transaction> {

    private static final Logger logger = LogManager.getLogger(TransactionDaoImpl.class);
    public static final String INSERT_NEW_TRANSACTION = "insert into transactions (bank_account_id_from, bank_account_id_to, sum) values (?, ?, ?)";

    public TransactionDaoImpl(ConnectionPool pool, Logger log) {
        super(pool, log);
    }

    @Override
    public Optional<Transaction> create(Transaction entity) throws DaoException {
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
            throw new DaoException("Transaction is not created", e);
        }
        return createdTransaction;
    }

    @Override
    public Optional<Transaction> read(Long id) throws DaoException {
        return Optional.empty();
    }

    @Override
    public List<Transaction> readAll() {
        return null;
    }

    @Override
    public Optional<Transaction> update(Transaction entity) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

//    private Transaction executeTransaction(ResultSet resultSet) throws SQLException {
//        return new Transaction(resultSet.getLong(ConfigurationManager.getProperty("table.id")),
//                resultSet.getLong(ConfigurationManager.getProperty("table.bank_account_id_from")),
//                resultSet.getLong(ConfigurationManager.getProperty("table.bank_account_id_to")),
//                resultSet.getBigDecimal(ConfigurationManager.getProperty("table:sum")));
//    }

    public static TransactionDaoImpl getInstance() {
        return TransactionDaoImpl.Holder.INSTANCE;
    }

    private static class Holder {
        public static final TransactionDaoImpl INSTANCE = new TransactionDaoImpl(ConnectionPool.lockingPool(), logger);
    }
}
