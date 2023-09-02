package org.example.clever_bank.service;

import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.ValidationException;

/**
 * The interface account service layer
 */
public interface AccountService extends Service<Account> {

    /**
     * Authenticate account
     *
     * @param login - account login
     * @param password - account password
     * @return account
     * @throws ValidationException
     */
    Account authenticate(String login, String password) throws ValidationException;

}
