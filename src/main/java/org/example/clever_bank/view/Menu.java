package org.example.clever_bank.view;

import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.AuthenticateException;

import java.util.Scanner;

public class Menu {

    public void getMenu() {

        Scanner scanner = new Scanner(System.in);

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
                    if (AuthMenu.getInstance().signUp()) {
                        System.out.println("You successfully registered!");
                        break;
                    } else {
                        System.out.println("System error");
                    }
                    break;
                case 2:
                    Account account;
                    try {
                        account = AuthMenu.getInstance().signIn();
                    } catch (AuthenticateException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                    if (account == null) {
                        System.out.println("System error");
                        continue;
                    }
                    while (true) {
                        System.out.println("Welcome to CleverBank");
                        System.out.println("Select an action:");
                        System.out.println("1. Replenishment of the account.");
                        System.out.println("2. Withdrawal.");
                        System.out.println("3. Transfer money to another customer's account.");
                        System.out.println("4. Transfer money to a customer of another bank.");
                        System.out.println("5. Log out.");
                        System.out.println("Enter your choice: ");
                        choice = scanner.nextInt();
                        scanner.nextLine();
                        switch (choice) {
                            case 1:
                                System.out.println(BankActivityMenu.getInstance().replenishmentAccount(account.getId()));
                                break;
                            case 2:
                                System.out.println(BankActivityMenu.getInstance().withdrawal(account.getId()));
                                break;
                            case 3:
                                System.out.println(BankActivityMenu.getInstance().internalTransfer(account.getId()));
                                break;
                            case 4:
                                System.out.println(BankActivityMenu.getInstance().externalTransfer(account.getId()));
                                break;
                            case 5:
                                continue outer;
                            default:
                                System.out.println("Enter correct number of action!");
                        }
                        break;
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

    public static Menu getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final Menu INSTANCE = new Menu();
    }
}
