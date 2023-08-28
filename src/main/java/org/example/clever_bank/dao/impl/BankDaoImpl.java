package org.example.clever_bank.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AbstractDao;
import org.example.clever_bank.dao.BankDao;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.exception.DaoException;
import org.example.clever_bank.util.ConfigurationManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankDaoImpl extends AbstractDao<Bank> implements BankDao<Bank> {

    private static final Logger logger = LogManager.getLogger(BankDaoImpl.class);

    public static final String INSERT_NEW_BANK = "insert into banks (name) values (?)";
    public static final String SELECT_BANK_BY_ID = "select id as id, name as name from banks where id=?";
    public static final String SELECT_ALL_BANKS = "select id as id, name as name from banks";
    public static final String UPDATE_BANK = "update banks set name=? where id=?";
    public static final String DELETE_BANK_BY_ID = "delete from banks where id=?";

    public BankDaoImpl(ConnectionPool pool, Logger log) {
        super(pool, log);
    }

    @Override
    public Optional<Bank> create(Bank bank) {
        logger.trace("start create bank");
        Optional<Bank> createdBank = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_BANK, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, bank.getName());
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines != 0) {
                final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long key = generatedKeys.getLong(1);
                    createdBank = Optional.of(Bank.builder()
                                    .id(key)
                                    .name(bank.getName())
                                    .build());
                }
            }
        } catch (SQLException e) {
            logger.error("sql error, could not create bank", e);
            throw new DaoException("Bank is not created", e);
        }
        return createdBank;
    }

    @Override
    public Optional<Bank> read(Long id) {
        logger.trace("start read bank");
        Optional<Bank> readBank = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BANK_BY_ID)) {
            preparedStatement.setLong(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final Bank bank = executeBank(resultSet);
                return Optional.of(bank);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not get bank", e);
            throw new DaoException("Bank is not read", e);
        }
        return readBank;
    }

    @Override
    public List<Bank> readAll() {
        logger.trace("start readAll");
        List<Bank> banks = new ArrayList<>();
        try (final Connection connection = pool.takeConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(SELECT_ALL_BANKS)) {
            while (resultSet.next()) {
                final Bank bank = executeBank(resultSet);
                banks.add(bank);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not get banks", e);
            throw new DaoException("Bank is not read", e);
        }
        return banks;
    }

    @Override
    public Optional<Bank> update(Bank bank) {
        logger.trace("start update bank");
        Optional<Bank> updatedBank = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BANK)) {
            preparedStatement.setString(1, bank.getName());
            preparedStatement.setLong(2, bank.getId());
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines > 0) {
                updatedBank = Optional.of(bank);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not update bank", e);
            throw new DaoException("Bank is not updated", e);
        }
        return updatedBank;
    }

    @Override
    public boolean delete(Long id) {
        logger.trace("start delete bank");
        boolean deleteBank = false;
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BANK_BY_ID)) {
            preparedStatement.setLong(1, id);
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines != 0) {
                deleteBank = true;
            }
        } catch (SQLException e) {
            logger.error("sql error, could not delete bank", e);
            throw new DaoException("Bank is not deleted", e);
        }
        return deleteBank;
    }

    private Bank executeBank(ResultSet resultSet) throws SQLException {
            return new Bank(resultSet.getLong(ConfigurationManager.getProperty("table.id")),
                    resultSet.getString(ConfigurationManager.getProperty("table.name")),
                    List.of());
    }

    public static BankDaoImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final BankDaoImpl INSTANCE = new BankDaoImpl(ConnectionPool.lockingPool(), logger) {
        };
    }
}
