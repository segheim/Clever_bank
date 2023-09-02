package org.example.clever_bank.dao;

import org.example.clever_bank.entity.BankAccount;

import java.util.List;
import java.util.Optional;

/**
 * The interface bank account dao layer
 */
public interface BankAccountDao extends DaoBase<BankAccount> {

    /**
     * Search bank account in database by account id and bank id
     *
     * @param id - account id
     * @param bankId - bank id
     * @return Optional<BankAccount>
     */
    Optional<BankAccount> readByAccountIdAndBankId(Long id, Long bankId);

    /**
     * Search bank account in database by account id and bank id
     *
     * @param login - account login
     * @param bankId - bank id
     * @return Optional<BankAccount>
     */
    Optional<BankAccount> readByAccountLoginAndBankId(String login, Long bankId);

    /**
     * Search bank account in database by bank id
     *
     * @param bankId - bank id
     * @return List<BankAccount>
     */
    List<BankAccount> readByBankId(Long bankId);

    /**
     * Creat bank account and bank in database
     *
     * @param bankId -bank id
     * @param bankAccountId - bank account id
     * @return boolean
     */
    boolean createBankBankAccount(Long bankId, Long bankAccountId);

}
