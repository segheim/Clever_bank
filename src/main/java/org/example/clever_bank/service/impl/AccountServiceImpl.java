package org.example.clever_bank.service.impl;

import org.example.clever_bank.dao.AccountDao;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.entity.Loggable;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.AccountService;
import org.example.clever_bank.service.BankAccountService;
import org.example.clever_bank.util.ConfigurationManager;
import org.example.clever_bank.validation.Validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;
    private final BankAccountService bankAccountService;

    public AccountServiceImpl(AccountDao accountDao, BankAccountService bankAccountService) {
        this.accountDao = accountDao;
        this.bankAccountService = bankAccountService;
    }

    @Override
    @Loggable
    public Account add(Account account) throws ValidationException {
        if (!Validator.getInstance().validateLogin(account.getLogin()) ||
                !Validator.getInstance().validatePassword(account.getPassword())) {
            throw new ValidationException("Login or password is not valid");
        }
        if (accountDao.readByLogin(account.getLogin()).isPresent()) {
            throw new ServiceException(String.format("Account login=%s is present", account.getLogin()));
        }

        Account saveAccount = accountDao.create(account).orElseThrow(() -> new ServiceException("Account is not created"));
        bankAccountService.add(BankAccount.builder()
                .banks(List.of(Bank.builder()
                                .id(Long.valueOf(ConfigurationManager.getProperty("bank.id")))
                                .name(ConfigurationManager.getProperty("bank.name"))
                                .build()))
                .account(saveAccount)
                .balance(BigDecimal.ZERO)
                .build());
        return saveAccount;
    }

    @Override
    @Loggable
    public Account findById(Long id) {
        return accountDao.read(id).orElseThrow(() -> new NotFoundEntityException(String.format("Account with id=%d is not found", id)));
    }

    @Override
    @Loggable
    public List<Account> findAll() {
        List<Account> accounts = accountDao.readAll();
        if (accounts.isEmpty()) {
            throw new ServiceException("Empty");
        }
        return accounts;
    }

    @Override
    @Loggable
    public Account update(Account account) throws ValidationException {
        if (!Validator.getInstance().validateLogin(account.getLogin()) ||
                !Validator.getInstance().validatePassword(account.getPassword())) {
            throw new ValidationException("Login or password is not valid");
        }
        accountDao.read(account.getId())
                .orElseThrow(() -> new NotFoundEntityException(String.format("Account with id=%d is not found", account.getId())));
        return accountDao.update(account).orElseThrow(() -> new ServiceException("Account is not updated"));
    }

    @Override
    @Loggable
    public boolean remove(Long id) {
        accountDao.read(id).orElseThrow(() -> new NotFoundEntityException(String.format("Account with id=%d is not found", id)));
        return accountDao.delete(id);
    }

    @Override
    @Loggable
    public Account authenticate(String login, String password) throws ValidationException {
        Optional<Account> optionalAccount = accountDao.readByLogin(login);
        if (optionalAccount.isEmpty()) {
            throw new NotFoundEntityException(String.format("Account with login=%s is not found", login));
        }
        if (!optionalAccount.get().getPassword().equals(password)) {
            throw new ValidationException("Incorrect password");
        }
        return optionalAccount.get();
    }
}
