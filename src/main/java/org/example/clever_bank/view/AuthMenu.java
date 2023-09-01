package org.example.clever_bank.view;

import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.AuthenticateException;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.AccountService;

import java.util.Optional;
import java.util.Scanner;

public class AuthMenu {

    private final Scanner scanner;
    private final AccountService accountService;

    public AuthMenu(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    public Account signIn() throws AuthenticateException, ValidationException {
        System.out.println("Enter login: ");
        String login = scanner.next();
        scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.next();
        scanner.nextLine();
        return accountService.authenticate(login, password);
            }

    public Account signUp() throws ValidationException {
        System.out.println("Enter login: ");
        String login = scanner.next();
        scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.next();
        scanner.nextLine();
        return accountService.add(Account.builder()
                .login(login)
                .password(password)
                .build());

    }
}
