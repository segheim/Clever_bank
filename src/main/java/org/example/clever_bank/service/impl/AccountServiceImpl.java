package org.example.clever_bank.service.impl;

import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.AccountService;
import org.example.clever_bank.validation.Validator;

import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private final AccountDaoImpl accountDao;

    public AccountServiceImpl(AccountDaoImpl accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account add(Account account) throws ValidationException {
        if (!Validator.getInstance().validateLogin(account.getLogin()) ||
                !Validator.getInstance().validatePassword(account.getPassword())) {
            throw new ValidationException("Login or password is not valid");
        }
        accountDao.readByLogin(account.getLogin())
                .orElseThrow(() -> new NotFoundEntityException(String.format("Account with login=%s", account.getLogin())));
        return accountDao.create(account).orElseThrow(() -> new ServiceException("Account is not created"));
    }

    @Override
    public Account findById(Long id) {
        return accountDao.read(id).orElseThrow(() -> new NotFoundEntityException(String.format("Account with id=%d is not found", id)));
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = accountDao.readAll();
        if (accounts.isEmpty()) {
            throw new ServiceException("Empty");
        }
        return accounts;
    }

    @Override
    public Account update(Account account) throws ValidationException {
        if (!Validator.getInstance().validateLogin(account.getLogin()) ||
                !Validator.getInstance().validatePassword(account.getPassword())) {
            throw new ValidationException("Login or password is not valid");
        }
        accountDao.read(account.getId()).orElseThrow(() -> new NotFoundEntityException(String.format("Account with id=%d is not found", account.getId())));
        return accountDao.update(account).orElseThrow(() -> new ServiceException("Account is not updated"));
    }

    @Override
    public boolean remove(Long id) {
        accountDao.read(id).orElseThrow(() -> new NotFoundEntityException(String.format("Account with id=%d is not found", id)));
        return accountDao.delete(id);
    }

    @Override
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
