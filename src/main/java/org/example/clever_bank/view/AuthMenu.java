package org.example.clever_bank.view;

import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.AuthenticateException;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.AccountService;
import org.example.clever_bank.service.impl.AccountServiceImpl;

import java.util.Optional;
import java.util.Scanner;

public class AuthMenu {

    private final Scanner scanner;
    private final AccountService accountService;

    public AuthMenu(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    public Optional<Account> signIn() throws AuthenticateException {
        System.out.println("Enter login: ");
        String login = scanner.next();
        scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.next();
        scanner.nextLine();
        Account authenticate;
        try {
            authenticate = accountService.authenticate(login, password);
        } catch (ServiceException | NotFoundEntityException | ValidationException e) {
            return Optional.empty();
        }
        return Optional.of(authenticate);
    }

    public Optional<Account> signUp() {
        System.out.println("Enter login: ");
        String login = scanner.next();
        scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.next();
        scanner.nextLine();
        try {
            return Optional.of(accountService.add(Account.builder()
                                    .login(login)
                                    .password(password)
                                    .build()));
        } catch (ValidationException e) {
            return Optional.empty();
        }
    }

    public static AuthMenu getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final AuthMenu INSTANCE = new AuthMenu(new Scanner(System.in), AccountServiceImpl.getInstance());
    }
}
