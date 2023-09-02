package org.example.clever_bank.service;

import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountDaoImpl accountDao;
    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private Account expected;
    private String login;
    private String password;

    @BeforeEach
    public void init() {

        long id = 1l;
        login = "Semenovich";
        password = "semen";
        account = Account.builder().
                login(login)
                .password(password)
                .build();

        expected = Account.builder()
                .id(id)
                .login(login)
                .password(password)
                .build();
    }

    @Test
    public void test_add_shouldEnterAccountToAccountDao_whenEnterCorrectData() throws ValidationException {

        Mockito.when(accountDao.create(account)).thenReturn(Optional.of(expected));
        Mockito.when(accountDao.readByLogin(login)).thenReturn(Optional.empty());
        Account actual = accountService.add(account);

        assertEquals(expected, actual);
        verify(accountDao).create(account);
    }

    @Test
    public void test_add_shouldThrowException_whenEnterIncorrectPassword() {

        account.setPassword("s");

        ValidationException validationException = assertThrows(ValidationException.class, () -> accountService.add(account));

        assertEquals(validationException.getMessage(), "Login or password is not valid");
    }

    @Test
    public void test_add_shouldThrowException_whenEnterAccountIsPresent() {

        Mockito.when(accountDao.readByLogin(login)).thenReturn(Optional.of(expected));
        ServiceException exception = assertThrows(ServiceException.class, () -> accountService.add(account));

        assertEquals(exception.getMessage(), String.format("Account login=%s is present", login));
    }

    @Test
    public void test_add_shouldThrowException_whenNotAddAccount() throws ValidationException {

        Mockito.when(accountDao.create(account)).thenReturn(Optional.empty());
        Mockito.when(accountDao.readByLogin(login)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> accountService.add(account));
        assertEquals(exception.getMessage(), "Account is not created");
        verify(accountDao).create(account);
    }

}
