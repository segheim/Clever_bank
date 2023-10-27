package org.example.clever_bank.service;

import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.impl.AccountServiceImpl;
import org.example.clever_bank.util.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;
    @Mock
    private AccountDaoImpl accountDao;
    @Mock
    private BankAccountService bankAccountService;
    @Spy
    private ConnectionPool connectionPool;

    private Account account;
    private Account expected;
    private BankAccount bankAccount;
    private Long id;
    private String login;
    private String password;

    @BeforeEach
    public void init() {
        ConnectionPool.lockingPool().init();
        id = 1l;
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

        bankAccount = BankAccount.builder()
                .banks(List.of(Bank.builder()
                        .id(id)
                        .name(login)
                        .build()))
                .account(account)
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    public void test_add_shouldEnterAccountToAccountDao_whenEnterCorrectData() throws ValidationException {
        Mockito.when(accountDao.readByLogin(login)).thenReturn(Optional.empty());
        Mockito.when(accountDao.create(account)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountService.add(any())).thenReturn(bankAccount);
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

    @Test
    public void test_findById_shouldFindAccount_whenEnterId(){
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(expected));
        Account actual = accountService.findById(id);

        assertEquals(expected, actual);
        verify(accountDao).read(id);
    }

    @Test
    public void test_findById_shouldThrowException_whenNotFindAccount() {
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> accountService.findById(id));
        assertEquals(exception.getMessage(), String.format("Account with id=%d is not found", id));
        verify(accountDao).read(id);
    }

    @Test
    public void test_add_shouldFindAccounts(){
        List<Account> expectedList = List.of(expected);

        Mockito.when(accountDao.readAll()).thenReturn(expectedList);
        List<Account> actualList = accountService.findAll();

        assertEquals(expectedList, actualList);
        verify(accountDao).readAll();
    }

    @Test
    public void test_add_shouldThrowException_whenNotAccounts(){
        Mockito.when(accountDao.readAll()).thenReturn(List.of());

        ServiceException exception = assertThrows(ServiceException.class, () -> accountService.findAll());
        assertEquals(exception.getMessage(), "Empty");
        verify(accountDao).readAll();
    }

    @Test
    public void test_update_shouldUpdateAccount_whenEnterCorrectData() throws ValidationException {
        Mockito.when(accountDao.update(expected)).thenReturn(Optional.of(expected));
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(expected));
        Account actual = accountService.update(expected);

        assertEquals(expected, actual);
        verify(accountDao).update(expected);
    }

    @Test
    public void test_update_shouldThrowException_whenEnterIncorrectPassword() {
        expected.setPassword("s");
        ValidationException validationException = assertThrows(ValidationException.class, () -> accountService.update(expected));

        assertEquals(validationException.getMessage(), "Login or password is not valid");
    }

    @Test
    public void test_update_shouldThrowException_whenEnterAccountIsAbsent() {

        Mockito.when(accountDao.read(id)).thenReturn(Optional.empty());
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> accountService.update(expected));

        assertEquals(exception.getMessage(), String.format("Account with id=%d is not found", expected.getId()));
    }

    @Test
    public void test_update_shouldThrowException_whenNotUpdateAccount(){
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(expected));

        ServiceException exception = assertThrows(ServiceException.class, () -> accountService.update(expected));
        assertEquals(exception.getMessage(), "Account is not updated");
        verify(accountDao).update(expected);
    }

    @Test
    public void test_remove_shouldRemoveAccount_whenEnterId(){
        boolean expectedBoolean = true;
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(expected));
        Mockito.when(accountDao.delete(id)).thenReturn(true);
        boolean actual = accountService.remove(id);

        assertEquals(expectedBoolean, actual);
        verify(accountDao).delete(id);
    }

    @Test
    public void test_remove_shouldThrowException_whenNotFindAccount() {
        Mockito.when(accountDao.read(id)).thenReturn(Optional.empty());

        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> accountService.remove(id));
        assertEquals(exception.getMessage(), String.format("Account with id=%d is not found", id));
        verify(accountDao).read(id);
    }

    @Test
    public void test_remove_shouldThrowException_whenNotUpdateAccount() {
        boolean expectedBoolean = false;
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(expected));
        Mockito.when(accountDao.delete(id)).thenReturn(false);

        boolean actualBoolean = accountService.remove(id);
        assertEquals(expectedBoolean, expectedBoolean);
        verify(accountDao).delete(id);
    }

    @Test
    public void test_authenticate_shouldThrowException_whenNotFindAccount() {
        Mockito.when(accountDao.readByLogin(login)).thenReturn(Optional.empty());

        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> accountService.authenticate(login, password));
        assertEquals(exception.getMessage(), String.format("Account with login=%s is not found", login));
    }

    @Test
    public void test_authenticate_shouldThrowException_whenNotMatchPasswords() {
        account.setPassword("dert");
        Mockito.when(accountDao.readByLogin(login)).thenReturn(Optional.of(account));

        ValidationException exception = assertThrows(ValidationException.class, () -> accountService.authenticate(login, password));
        assertEquals(exception.getMessage(), "Incorrect password");
    }

    @Test
    public void test_authenticate_shouldGetAccount_whenEnterCorrectLoginPassword() throws ValidationException {
        account.setId(id);
        Mockito.when(accountDao.readByLogin(login)).thenReturn(Optional.of(account));

        Account actual = accountService.authenticate(login, password);
        assertEquals(expected, actual);
        verify(accountDao).readByLogin(login);
    }
}
