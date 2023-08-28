package org.example.clever_bank.service.impl;

import org.example.clever_bank.dao.AccountDao;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.service.AccountService;

import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private final AccountDaoImpl accountDao;

    public AccountServiceImpl(AccountDaoImpl accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public boolean add(Account account) {
        return false;
    }

    @Override
    public Account findById(Long id) {
        return null;
    }

    @Override
    public List<Account> findAll() {
        return null;
    }

    @Override
    public boolean update(Account account) {
        return false;
    }

    @Override
    public boolean remove(Long id) {
        return false;
    }

    @Override
    public Account authenticate(String login, String password) {
        Optional<Account> optionalAccount = accountDao.readByLogin(login);
        if (optionalAccount.isEmpty()) {
            throw new NotFoundEntityException(String.format("Account with login=%s is not found", login));
        }
        if(!optionalAccount.get().getPassword().equals(password)) {
            throw new ServiceException("Incorrect password");
        }
        return optionalAccount.get();
    }

    public static AccountServiceImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final AccountServiceImpl INSTANCE = new AccountServiceImpl(AccountDaoImpl.getInstance());
    }
}
