package org.example.clever_bank.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AbstractDao;
import org.example.clever_bank.dao.BankAccountDao;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.util.ConfigurationManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankAccountDaoImpl extends AbstractDao<BankAccount> implements BankAccountDao {

    private static final Logger logger = LogManager.getLogger(BankAccountDaoImpl.class);

    public static final String INSERT_NEW_BANK_ACCOUNT = "insert into bank_accounts (account_id, balance) values (?,?)";
    public static final String SELECT_BANK_ACCOUNT_BY_ID = "select ba.id as id, ba.balance as balance, ba.date_create as date_create," +
            " a.id as owner_account_id, a.login as owner_account_login from bank_accounts as ba join accounts a on a.id = ba.account_id" +
            " join banks_bank_accounts bba on ba.id = bba.bank_account_id join banks b on b.id = bba.bank_id where ba.id=?";
    public static final String SELECT_ALL_BANK_ACCOUNTS = "select ba.id as id, ba.balance as balance, ba.date_create as date_create," +
            " a.id as owner_account_id, a.login as owner_account_login from bank_accounts as ba join accounts a on a.id = ba.account_id" +
            " join banks_bank_accounts bba on ba.id = bba.bank_account_id join banks b on b.id = bba.bank_id";
    public static final String SELECT_BANK_ACCOUNTS_BY_BANK_ID = "select ba.id as id, ba.balance as balance," +
            " ba.date_create as date_create, a.id as owner_account_id, a.login as owner_account_login" +
            " from bank_accounts as ba join accounts a on a.id = ba.account_id" +
            " join banks_bank_accounts bba on ba.id = bba.bank_account_id join banks b on b.id = bba.bank_id where b.id=?";
    public static final String SELECT_BANK_ACCOUNT_BY_ACCOUNT_ID_AND_BANK_ID = "select ba.id as id, ba.balance as balance, ba.date_create as date_create," +
            " a.id as owner_account_id, a.login as owner_account_login from bank_accounts as ba join accounts a on a.id = ba.account_id" +
            " join banks_bank_accounts bba on ba.id = bba.bank_account_id join banks b on b.id = bba.bank_id where a.id=? and b.id=?";
    public static final String SELECT_BANK_ACCOUNT_BY_ACCOUNT_LOGIN_AND_BANK_ID = "select ba.id as id, ba.balance as balance, ba.date_create as date_create," +
            " a.id as owner_account_id, a.login as owner_account_login from bank_accounts as ba join accounts a on a.id = ba.account_id" +
            " join banks_bank_accounts bba on ba.id = bba.bank_account_id join banks b on b.id = bba.bank_id where a.login=? and b.id=?";
    public static final String UPDATE_BANK_ACCOUNT = "update bank_accounts set balance=? where id=?";
    public static final String DELETE_BANK_ACCOUNT = "delete from bank_accounts where id=?";
    public static final String INSERT_NEW_BANK_BANK_ACCOUNT = "insert into banks_bank_accounts (bank_id, bank_account_id) values (?,?)";

    public BankAccountDaoImpl(ConnectionPool pool, Logger log) {
        super(pool, log);
    }

    @Override
    public Optional<BankAccount> create(BankAccount entity) {
        logger.trace("start create bank account");
        Optional<BankAccount> createdBankAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_BANK_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, entity.getAccount().getId());
            preparedStatement.setBigDecimal(2, entity.getBalance());
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines != 0) {
                final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long key = generatedKeys.getLong(1);
                    createdBankAccount = Optional.of(BankAccount.builder()
                            .id(key)
                            .account(entity.getAccount())
                            .balance(entity.getBalance())
                            .build());
                }
            }
        } catch (SQLException e) {
            logger.error("sql error, could not create bank account", e);
        }
        return createdBankAccount;
    }

    @Override
    public Optional<BankAccount> read(Long id) {
        logger.trace("start read bank account");
        Optional<BankAccount> readBankAccount = Optional.empty();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BANK_ACCOUNT_BY_ID)) {
            preparedStatement.setLong(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final BankAccount bankAccount = executeBankAccount(resultSet);
                return Optional.of(bankAccount);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not find bank account", e);
        }
        return readBankAccount;
    }

    @Override
    public List<BankAccount> readAll() {
        logger.trace("start read all bank account");
        List<BankAccount> bankAccounts = new ArrayList<>();
        try (final Connection connection = pool.takeConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(SELECT_ALL_BANK_ACCOUNTS)) {
            while (resultSet.next()) {
                BankAccount bankAccount = executeBankAccount(resultSet);
                bankAccounts.add(bankAccount);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not found bankAccounts", e);
        }
        return bankAccounts;
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
        }
        return updatedBankAccount;
    }

    @Override
    public boolean delete(Long id) {
        logger.trace("start delete bank account");
        boolean deleteBankAccount = false;
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BANK_ACCOUNT)) {
            preparedStatement.setLong(1, id);
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines != 0) {
                deleteBankAccount = true;
            }
        } catch (SQLException e) {
            logger.error("sql error, could not delete bank account", e);
        }
        return deleteBankAccount;
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
        }
        return readBankAccount;
    }

    @Override
    public List<BankAccount> readByBankId(Long bankId) {
        logger.trace("start read bank accounts by bank id");
        List<BankAccount> bankAccounts = new ArrayList<>();
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BANK_ACCOUNTS_BY_BANK_ID)) {
            preparedStatement.setLong(1, bankId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BankAccount bankAccount = executeBankAccount(resultSet);
                bankAccounts.add(bankAccount);
            }
        } catch (SQLException e) {
            logger.error("sql error, could not found bankAccounts", e);
        }
        return bankAccounts;
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
        }
        return readBankAccount;
    }

    @Override
    public boolean createBankBankAccount(Long bankId, Long bankAccountId) {
        logger.trace("start create bank - bank account");
        boolean createdBankBankAccount = false;
        try (final Connection connection = pool.takeConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_BANK_BANK_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, bankId);
            preparedStatement.setLong(2, bankAccountId);
            final int numberChangedLines = preparedStatement.executeUpdate();
            if (numberChangedLines > 0) {
                createdBankBankAccount = true;
            }
        } catch (SQLException e) {
            logger.error("sql error, could not create bank account", e);
        }
        return createdBankBankAccount;
    }

    private BankAccount executeBankAccount(ResultSet resultSet) throws SQLException {
        return BankAccount.builder()
                .id(resultSet.getLong(ConfigurationManager.getProperty("table.id")))
                .balance(resultSet.getBigDecimal(ConfigurationManager.getProperty("table.balance")))
                .account(Account.builder()
                        .id(resultSet.getLong(ConfigurationManager.getProperty("table.owner_account_id")))
                        .login(resultSet.getString(ConfigurationManager.getProperty("table.owner_account_login")))
                        .build())
                .dateCreate(resultSet.getTimestamp(ConfigurationManager.getProperty("table.date_create")).toLocalDateTime())
                .build();
    }
}
