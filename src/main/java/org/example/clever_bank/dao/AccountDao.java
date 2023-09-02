package org.example.clever_bank.dao;

import org.example.clever_bank.entity.Account;

import java.util.Optional;

/**
 * The interface Account dao layer
 */
public interface AccountDao extends DaoBase<Account> {

    /**
     * Search account in database by login
     *
     * @param login - account login
     * @return Optional<Account>
     */
    Optional<Account> readByLogin(String login);

}
