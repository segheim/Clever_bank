package org.example.clever_bank.dao;

import org.example.clever_bank.entity.Bank;

import java.util.Optional;

/**
 * The interface Bank dao layer
 */
public interface BankDao extends DaoBase<Bank> {

    /**
     * Search bank in database by name
     *
     * @param name - name
     * @return Optional<Bank>
     */
    Optional<Bank> readByName(String name);
}
