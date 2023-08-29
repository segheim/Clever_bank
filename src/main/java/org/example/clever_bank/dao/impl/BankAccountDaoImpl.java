package org.example.clever_bank.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AbstractDao;
import org.example.clever_bank.dao.BankAccountDao;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.exception.DaoException;
import org.example.clever_bank.util.ConfigurationManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BankAccountDaoImpl extends AbstractDao<BankAccount> implements BankAccountDao {

    private static final Logger logger = LogManager.getLogger(BankAccountDaoImpl.class);

    public static final String SELECT_BANK_ACCOUNT_BY_ACCOUNT_ID_AND_BANK_ID = "select ba.id as id, ba.balance as balance" +
            " from bank_accounts as ba join accounts a on a.id = ba.account_id join banks_bank_accounts bba on ba.id = " +
            "bba.bank_account_id join banks b on b.id = bba.bank_id where a.id=? and b.id=?";
    public static final String SELECT_BANK_ACCOUNT_BY_ACCOUNT_LOGIN_AND_BANK_ID = "select ba.id as id, ba.balance as balance" +
            " from bank_accounts as ba join accounts a on a.id = ba.account_id join banks_bank_accounts bba on ba.id = " +
            "bba.bank_account_id join banks b on b.id = bba.bank_id where a.login=? and b.id=?";
    public static final String UPDATE_BANK_ACCOUNT = "update bank_accounts set balance=? where id=?";

    private BankAccountDaoImpl(ConnectionPool pool, Logger log) {
        super(pool, log);
    }

    @Override
    public Optional<BankAccount> create(BankAccount entity) throws DaoException {
        return Optional.empty();
    }

    @Override
    public Optional<BankAccount> read(Long id) throws DaoException {
        return Optional.empty();
    }

    @Override
    public List<BankAccount> readAll() {
        return null;
    }

    @Override
    public Optional<BankAccount> update(BankAccount entity) {
        logger.trace("start update bank account");
        Optional<BankAccount> updatedBankAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BANK_ACCOUNT)) {
            preparedStatement.setBigDecimal(1, entity.getBalance());
            preparedStatement.setLong(2, entity.getId());
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines > 0) {
                updatedBankAccount = Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not update account", e);
            throw new DaoException("Bank account is not updated", e);
        }
        return updatedBankAccount;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Optional<BankAccount> readByAccountIdAndBankId(Long id, Long bankId) {
        logger.trace("start read bank account by id and bank id");
        Optional<BankAccount> readBankAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BANK_ACCOUNT_BY_ACCOUNT_ID_AND_BANK_ID)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, bankId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final BankAccount bankAccount = executeBankAccount(resultSet);
                return Optional.of(bankAccount);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not get bank account", e);
            throw new DaoException("Bank Account is not read", e);
        }
        return readBankAccount;
    }

    @Override
    public Optional<BankAccount> readByAccountLoginAndBankId(String login, Long bankId) {
        logger.trace("start read bank account by login and bank id");
        Optional<BankAccount> readBankAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BANK_ACCOUNT_BY_ACCOUNT_LOGIN_AND_BANK_ID)) {
            preparedStatement.setString(1, login);
            preparedStatement.setLong(2, bankId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final BankAccount bankAccount = executeBankAccount(resultSet);
                return Optional.of(bankAccount);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not get bank account", e);
            throw new DaoException("Bank Account is not read", e);
        }
        return readBankAccount;
    }

    private BankAccount executeBankAccount(ResultSet resultSet) throws SQLException {
        return BankAccount.builder()
                .id(resultSet.getLong(ConfigurationManager.getProperty("table.id")))
                .balance(resultSet.getBigDecimal(ConfigurationManager.getProperty("table.balance")))
                .build();
    }

    public static BankAccountDaoImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final BankAccountDaoImpl INSTANCE = new BankAccountDaoImpl(ConnectionPool.lockingPool(), logger);
    }
}
