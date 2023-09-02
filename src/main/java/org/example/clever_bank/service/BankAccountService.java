package org.example.clever_bank.service;

import org.example.clever_bank.entity.BankAccount;

import java.math.BigDecimal;

/**
 * The interface bank account service layer
 */
public interface BankAccountService extends Service<BankAccount> {

    /**
     * Replenishment of bank account of customer on amount of money by account id
     *
     * @param accountId - account id
     * @param amount - amount of money
     * @return bank account
     */
    BankAccount replenishmentAccount(Long accountId, BigDecimal amount);

    /**
     * Withdrawal of bank account of customer on amount of money by account id
     * @param accountId  - account id
     * @param moneyAmount - amount of money
     * @return bank account
     */
    BankAccount withdrawal(Long accountId, BigDecimal moneyAmount);

    /**
     * Transfer money from bank account to bank account
     *
     * @param ownerId - account id of holder
     * @param bankId - bank id to whom we want to transfer
     * @param loginUser - login to whom we want to transfer
     * @param amount - amount of money
     * @return balance of holder
     */
    BigDecimal transferMoney(Long ownerId, Long bankId, String loginUser, BigDecimal amount);

    /**
     * Accrue a fix percent(from config file) on every user balance in last day of month
     */
    void accruePercentOnUserBalancesOfCleverBank();

}
