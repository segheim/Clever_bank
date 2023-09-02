package org.example.clever_bank.view;

import org.example.clever_bank.entity.Account;

import java.util.Scanner;

/**
 * Base menu Clever bank
 */
public class Menu {

    private final AuthMenu authMenu;
    private final BankActivityMenu bankActivityMenu;
    private final Scanner scanner;

    public Menu(AuthMenu authMenu, BankActivityMenu bankActivityMenu, Scanner scanner) {
        this.authMenu = authMenu;
        this.bankActivityMenu = bankActivityMenu;
        this.scanner = scanner;
    }

    /**
     * Display menu operations
     */
    public void getMenu() {

        outer:
        while (true) {
            System.out.println("Clever Bank");
            System.out.println("Select an action:");
            System.out.println("1. Sign up.");
            System.out.println("2. Sign in.");
            System.out.println("3. Exit.");
            System.out.println("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    try {
                        authMenu.signUp();
                    } catch (Exception e) {
                        System.out.println(String.format("Failed. %s", e.getMessage()));
                        break;
                    }
                    System.out.println("You successfully registered!");
                    break;
                case 2:
                    Account account;
                    try {
                        account = authMenu.signIn();
                    } catch (Exception e) {
                        System.out.println(String.format("Authentication is failed. %s", e.getMessage()));
                        continue;
                    }
                    while (true) {
                        System.out.println("Welcome to CleverBank");
                        System.out.println("Select an action:");
                        System.out.println("1. Replenishment of the account.");
                        System.out.println("2. Withdrawal.");
                        System.out.println("3. Transfer money to another customer's account.");
                        System.out.println("4. Transfer money to a customer of another bank.");
                        System.out.println("5. Statement of account.");
                        System.out.println("6. Log out.");
                        System.out.println("Enter your choice: ");
                        choice = scanner.nextInt();
                        scanner.nextLine();
                        switch (choice) {
                            case 1:
                                System.out.println(bankActivityMenu.replenishmentAccount(account.getId()));
                                break;
                            case 2:
                                System.out.println(bankActivityMenu.withdrawal(account.getId()));
                                break;
                            case 3:
                                System.out.println(bankActivityMenu.internalTransfer(account.getId()));
                                break;
                            case 4:
                                System.out.println(bankActivityMenu.externalTransfer(account.getId()));
                                break;
                            case 5:
                                System.out.println(bankActivityMenu.createStatementOfAccount(account.getId()));
                                break;
                            case 6:
                                continue outer;
                            default:
                                System.out.println("Enter correct number of action!");
                        }
                    }
                case 3:
                    System.out.println("\nBye!");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Enter correct number of action!");
            }
        }
    }
}
