package org.example.clever_bank.service;

import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.ValidationException;

public interface AccountService extends Service<Account> {

    Account authenticate(String login, String password) throws ValidationException;
//
//    static AccountService getInstance() {
//        return AccountService.getInstance();
//    }

}
