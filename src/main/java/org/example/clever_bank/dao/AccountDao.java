package org.example.clever_bank.dao;

import org.example.clever_bank.entity.Account;

import java.util.Optional;

public interface AccountDao {

    Optional<Account> readByLogin(String login);
}
