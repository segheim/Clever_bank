package org.example.clever_bank.view;

import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.service.BankAccountService;
import org.example.clever_bank.service.impl.BankAccountServiceImpl;

import java.math.BigDecimal;
import java.util.Scanner;

public class BankActivityMenu {

    public static final long CLEVER_BANK_ID = 1L;

    private final Scanner scanner;
    private final BankAccountService bankAccountService;

    public BankActivityMenu(Scanner scanner, BankAccountService bankAccountService) {
        this.scanner = scanner;
        this.bankAccountService = bankAccountService;

    }

    public String replenishmentAccount(Long id) {
        System.out.println("Enter deposit amount: ");
        BigDecimal moneyAmount = scanner.nextBigDecimal();
        scanner.nextLine();
        BankAccount bankAccount;
        try {
            bankAccount = bankAccountService.replenishmentAccount(id, moneyAmount);
        } catch (Exception e) {
            return String.format("Operation is failed. %s", e.getMessage());
        }
        return String.format("Operation is completed. Balance=%s", bankAccount.getBalance().toString());
    }

    public String withdrawal(Long id) {
        System.out.println("Enter withdrawal amount: ");
        BigDecimal moneyAmount = scanner.nextBigDecimal();
        scanner.nextLine();
        BankAccount bankAccount;
        try {
            bankAccount = bankAccountService.withdrawal(id, moneyAmount);
        } catch (Exception e) {
            return String.format("Operation is failed. %s", e.getMessage());
        }
        return String.format("Operation is completed. Balance=%s", bankAccount.getBalance().toString());
    }

    public String internalTransfer(Long id) {
        System.out.println("Enter account login to whom you want to transfer money: ");
        String transferAccountLogin = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter transfer money amount: ");
        BigDecimal moneyAmount = scanner.nextBigDecimal();
        scanner.nextLine();
        BigDecimal balance;
        try {
            balance = bankAccountService.transferMoney(id, CLEVER_BANK_ID, transferAccountLogin, moneyAmount);
        } catch (Exception e) {
            return String.format("Operation is failed. %s", e.getMessage());
        }
        return String.format("Operation is completed. Balance=%s", balance.toString());
    }

    public String externalTransfer(Long id) {
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
        Long bankId = Long.valueOf(choice + 1);
        System.out.println("Enter account login for external transfer: ");
        String transferAccountLogin = scanner.nextLine();
        System.out.println("Enter transfer money amount: ");
        BigDecimal moneyAmount = scanner.nextBigDecimal();
        scanner.nextLine();
        BigDecimal balance;
        try {
            balance = bankAccountService.transferMoney(id, bankId, transferAccountLogin, moneyAmount);
        } catch (Exception e) {
            return String.format("Operation is failed. %s", e.getMessage());
        }
        return String.format("Operation is completed. Balance=%s", balance.toString());
    }
}
