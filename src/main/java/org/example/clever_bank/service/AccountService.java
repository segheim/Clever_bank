package org.example.clever_bank.service;

import org.example.clever_bank.entity.Account;

public interface AccountService extends Service<Account> {

    Account authenticate(String login, String password);

    static AccountService getInstance() {
        return AccountService.getInstance();
    }

}
