package org.example.clever_bank.view;

import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.service.BankAccountService;
import org.example.clever_bank.service.TransactionService;
import org.example.clever_bank.util.ConfigurationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Display Clever bank operations
 */
public class BankActivityMenu {

    public static final int NUMBER_FIRST_DAY = 1;
    public static final int DISPLACEMENT_FOR_ID = 1;

    private final Scanner scanner;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;

    public BankActivityMenu(Scanner scanner, BankAccountService bankAccountService, TransactionService transactionService) {
        this.scanner = scanner;
        this.bankAccountService = bankAccountService;

        this.transactionService = transactionService;
    }

    /**
     * Operation replenishment
     *
     * @param accountId account id
     * @return account balance
     */
    public String replenishmentAccount(Long accountId) {
        System.out.println("Enter deposit amount: ");
        BigDecimal moneyAmount = scanner.nextBigDecimal();
        scanner.nextLine();
        BankAccount bankAccount;
        try {
            bankAccount = bankAccountService.replenishmentAccount(accountId, moneyAmount);
        } catch (Exception e) {
            return String.format("Operation is failed. %s", e.getMessage());
        }
        return String.format("Operation is completed. Balance=%s", bankAccount.getBalance().toString());
    }

    /**
     * Operation withdrawal
     *
     * @param accountId - account id
     * @return account balance
     */
    public String withdrawal(Long accountId) {
        System.out.println("Enter withdrawal amount: ");
        BigDecimal moneyAmount = scanner.nextBigDecimal();
        scanner.nextLine();
        BankAccount bankAccount;
        try {
            bankAccount = bankAccountService.withdrawal(accountId, moneyAmount);
        } catch (Exception e) {
            return String.format("Operation is failed. %s", e.getMessage());
        }
        return String.format("Operation is completed. Balance=%s", bankAccount.getBalance().toString());
    }

    /**
     * Operation transfer internal Clever bank
     *
     * @param accountId - account id
     * @return account balance
     */
    public String internalTransfer(Long accountId) {
        System.out.println("Enter account login to whom you want to transfer money: ");
        String transferAccountLogin = scanner.nextLine();
        System.out.println("Enter transfer money amount: ");
        BigDecimal moneyAmount = scanner.nextBigDecimal();
        BigDecimal balance;
        try {
            balance = bankAccountService.transferMoney(accountId, Long.valueOf(ConfigurationManager.getProperty("bank.id")),
                    transferAccountLogin, moneyAmount);
        } catch (Exception e) {
            return String.format("Operation is failed. %s", e.getMessage());
        }
        return String.format("Operation is completed. Balance=%s", balance.toString());
    }

    /**
     * External transfer to another bank
     * @param accountId - account id
     * @return account balance
     */
    public String externalTransfer(Long accountId) {
        int choice;
        while (true) {
            System.out.println("Select a bank:");
            System.out.println("1. Good Bank.");
            System.out.println("2. Best Bank.");
            System.out.println("3. Full  Bank.");
            System.out.println("4. Big Bank.");
            System.out.println("5. Top Bank.");
            System.out.println("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            if (choice > 5 || choice < 1) {
                System.out.println("Enter correct number of action! Try again: ");
            } else {
                break;
            }
        }
        Long bankId = Long.valueOf(choice + DISPLACEMENT_FOR_ID);
        System.out.println("Enter account login for external transfer: ");
        String transferAccountLogin = scanner.nextLine();
        System.out.println("Enter transfer money amount: ");
        BigDecimal moneyAmount = scanner.nextBigDecimal();
        scanner.nextLine();
        BigDecimal balance;
        try {
            balance = bankAccountService.transferMoney(accountId, bankId, transferAccountLogin, moneyAmount);
        } catch (Exception e) {
            return String.format("Operation is failed. %s", e.getMessage());
        }
        return String.format("Operation is completed. Balance=%s", balance.toString());
    }

    /**
     * Create statement of account according select conditions
     *
     * @param accountId - account id
     * @return statement of account text
     */
    public String createStatementOfAccount(Long accountId) {
        while (true) {
            System.out.println("Select a period: ");
            System.out.println("1. Month.");
            System.out.println("2. Year.");
            System.out.println("3. All period.");
            System.out.println("Enter your choice: ");
            int choicePeriod = scanner.nextInt();
            scanner.nextLine();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startMonth = now.withDayOfMonth(NUMBER_FIRST_DAY);
            startMonth = startMonth.toLocalDate().atStartOfDay();
            LocalDateTime startYear = now.withDayOfYear(NUMBER_FIRST_DAY);
            startYear = startYear.toLocalDate().atStartOfDay();
            switch (choicePeriod) {
                case 1:
                    return fetchStatementOfAccount(accountId, startMonth, now);
                case 2:
                    return fetchStatementOfAccount(accountId, startYear, now);
                case 3:
                    return fetchStatementOfAccount(accountId, now, now);
                default:
                    System.out.println("Enter correct number of period!");
            }
        }
    }

    private String fetchStatementOfAccount(Long accountId, LocalDateTime periodFrom, LocalDateTime periodTo) {
        while (true) {
            System.out.println("Select the output save format: ");
            System.out.println("1. TXT.");
            System.out.println("2. PDF.");
            System.out.println("Enter your choice: ");
            int formatChoice = scanner.nextInt();
            scanner.nextLine();
            switch (formatChoice) {
                case 1:
                    return transactionService.createStatementOfAccount(accountId, periodFrom, periodTo);
                case 2:
                    return " ";
                default:
                    System.out.println("Enter correct number of format!");
            }
        }
    }
}
