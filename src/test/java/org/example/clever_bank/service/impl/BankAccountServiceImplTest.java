package org.example.clever_bank.service.impl;

import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.AccountDao;
import org.example.clever_bank.dao.BankAccountDao;
import org.example.clever_bank.dao.BankDao;
import org.example.clever_bank.dao.TransactionDao;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.service.text.PaperWorker;
import org.example.clever_bank.util.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;
    @Mock
    private BankAccountDao bankAccountDao;
    @Mock
    private TransactionDao transactionDao;
    @Mock
    private AccountDao accountDao;
    @Mock
    private BankDao bankDao;
    @Mock
    private PaperWorker paperWorker;
    @Mock
    private Transaction transaction;
    @Spy
    private ConnectionPool connectionPool;

    private BankAccount bankAccount;
    private BankAccount expected;
    private Account account;
    private Long id;
    private String login;
    private String password;
    private BigDecimal money;
    private long anotherId;
    private Bank bestBank;
    private String anotherLogin;
    private BankAccount anotherBankAccount;
    private Transaction transactionWithId;

    @BeforeEach
    public void init() {
        ConnectionPool.lockingPool().init();
        try (MockedStatic<ConfigurationManager> manager = Mockito.mockStatic(ConfigurationManager.class)) {
            manager.when(() -> ConfigurationManager.getProperty("operation.transaction")).thenReturn("transaction");
        }
        try (MockedStatic<ConfigurationManager> manager = Mockito.mockStatic(ConfigurationManager.class)) {
            manager.when(() -> ConfigurationManager.getProperty("operation.replenishment")).thenReturn("replenishment");
        }
        try (MockedStatic<ConfigurationManager> manager = Mockito.mockStatic(ConfigurationManager.class)) {
            manager.when(() -> ConfigurationManager.getProperty("accrual.percentage")).thenReturn("percentage");
        }
        id = 1l;
        anotherId = 2L;
        anotherLogin = "Groot";
        login = "Semenovich";
        password = "semen";
        money = BigDecimal.valueOf(1000);
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 20, 15, 30);

        account = Account.builder()
                .id(id)
                .login(login)
                .password(password)
                .build();

        bankAccount = BankAccount.builder()
                .account(account)
                .banks(List.of(Bank.builder()
                        .id(id)
                        .name("clever_bank")
                        .build()))
                .balance(money)
                .build();

        expected = BankAccount.builder()
                .id(id)
                .account(account)
                .banks(List.of(Bank.builder()
                        .id(id)
                        .name("clever_bank")
                        .build()))
                .balance(money)
                .build();

        transaction = Transaction.builder()
                .bankAccountFrom(bankAccount)
                .bankAccountTo(bankAccount)
                .sum(money)
                .type("Replenishment")
                .build();

        bestBank = Bank.builder()
                .id(anotherId)
                .name("best_bank")
                .build();

        anotherBankAccount = BankAccount.builder()
                .account(account)
                .banks(List.of(bestBank))
                .balance(money)
                .build();

        transactionWithId = Transaction.builder()
                .id(id)
                .bankAccountFrom(bankAccount)
                .bankAccountTo(bankAccount)
                .sum(money)
                .type("Replenishment")
                .dateCreate(dateTime)
                .build();
    }

    @Test
    public void test_add_shouldReturnBankAccountToBankAccountDao_whenEnterCorrectData() {
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(account));
        Mockito.when(bankAccountDao.create(bankAccount)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.createBankBankAccount(id, id)).thenReturn(true);

        BankAccount actual = bankAccountService.add(bankAccount);

        assertEquals(expected, actual);
        verify(bankAccountDao).create(bankAccount);
    }

    @Test
    public void test_add_shouldThrowException_whenEnterIncorrectAmount() {
        bankAccount.setBalance(bankAccount.getBalance().negate());
        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.add(bankAccount));

        assertEquals(serviceException.getMessage(), "Enter correct amount of money");
    }

    @Test
    public void test_add_shouldThrowException_whenAccountAbsent() {
        Mockito.when(accountDao.read(id)).thenReturn(Optional.empty());
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> bankAccountService.add(bankAccount));

        assertEquals(notFoundEntityException.getMessage(), "Account with id=1 is not present");
    }

    @Test
    public void test_add_shouldThrowException_whenBankAccountNotCreate() {
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(account));
        Mockito.when(bankAccountDao.create(bankAccount)).thenReturn(Optional.empty());
        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.add(bankAccount));

        assertEquals(serviceException.getMessage(), "could not create bank account. Bank account is not created");
    }

    @Test
    public void test_add_shouldThrowException_whenBankBankAccountNotCreate() {
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(account));
        Mockito.when(bankAccountDao.create(bankAccount)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.createBankBankAccount(id, id)).thenReturn(false);
        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.add(bankAccount));

        assertEquals(serviceException.getMessage(), "could not create bank account. BankBankAccount is not created");
    }

    @Test
    public void test_findById_shouldReturnBankAccount_whenEnterCorrectId() {
        Mockito.when(bankAccountDao.read(id)).thenReturn(Optional.of(expected));
        BankAccount actual = bankAccountService.findById(id);

        assertEquals(expected, actual);
        verify(bankAccountDao).read(id);
    }

    @Test
    public void test_add_shouldThrowException_whenEnterIncorrectId() {
        Mockito.when(bankAccountDao.read(id)).thenReturn(Optional.empty());
        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.findById(id));

        assertEquals(serviceException.getMessage(), "Bank account is not found");
    }

    @Test
    public void test_findByAll_shouldReturnBankAccounts() {
        List<BankAccount> expectedList = List.of(expected);
        Mockito.when(bankAccountDao.readAll()).thenReturn(expectedList);
        List<BankAccount> actual = bankAccountService.findAll();

        assertEquals(expectedList, actual);
        verify(bankAccountDao).readAll();
    }

    @Test
    public void test_findByAll_shouldThrowException_whenBankAccountsNotFound() {
        Mockito.when(bankAccountDao.readAll()).thenReturn(Collections.emptyList());
        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.findAll());

        assertEquals(serviceException.getMessage(), "Empty");
    }

    @Test
    public void test_update_shouldReturnBankAccount_whenEnterCorrectData() {
        Mockito.when(bankAccountDao.update(bankAccount)).thenReturn(Optional.of(expected));

        BankAccount actual = bankAccountService.update(bankAccount);

        assertEquals(expected, actual);
        verify(bankAccountDao).update(bankAccount);
    }

    @Test
    public void test_update_shouldThrowException_whenEnterIncorrectAmount() {
        bankAccount.setBalance(bankAccount.getBalance().negate());
        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.update(bankAccount));

        assertEquals(serviceException.getMessage(), "Enter correct amount of money");
    }

    @Test
    public void test_update_shouldThrowException_whenNotUpdated() {
        Mockito.when(bankAccountDao.update(bankAccount)).thenReturn(Optional.empty());
        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.update(bankAccount));

        assertEquals(serviceException.getMessage(), "Bank account is not updated");
    }

    @Test
    public void test_remove_shouldReturnTrue_whenEnterPresentId() {
        Mockito.when(bankAccountDao.delete(id)).thenReturn(true);
        assertTrue(bankAccountService.remove(id));
    }

    @Test
    public void test_remove_shouldReturnFalse_whenEnterAbsentId() {
        Mockito.when(bankAccountDao.delete(id)).thenReturn(false);
        assertFalse(bankAccountService.remove(id));
    }

    @Test
    public void test_replenishmentAccount_shouldReturnUpdatedBankAccount_whenEnterCorrectData() {
        try (MockedStatic<ConfigurationManager> manager = Mockito.mockStatic(ConfigurationManager.class)) {
            manager.when(() -> ConfigurationManager.getProperty("operation.replenishment")).thenReturn("replenishment");
        }
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 20, 15, 30);
        Transaction transactionWithId = Transaction.builder()
                .id(id)
                .bankAccountFrom(bankAccount)
                .bankAccountTo(bankAccount)
                .sum(money)
                .type("Replenishment")
                .dateCreate(dateTime)
                .build();

        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        expected.setBalance(expected.getBalance().add(money));
        Mockito.when(bankAccountDao.update(expected)).thenReturn(Optional.of(expected));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transactionWithId));
        Mockito.when(transactionDao.read(id)).thenReturn(Optional.of(transactionWithId));
        BankAccount actual = bankAccountService.replenishmentAccount(id, money);

        expected.setBalance(expected.getBalance().add(money));
        expected.setId(id);

        assertEquals(expected, actual);
    }

    @Test
    public void test_replenishmentAccount_shouldThrowException_whenAbsentAccountId() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.replenishmentAccount(id, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Not found bank account");
    }

    @Test
    public void test_replenishmentAccount_shouldThrowException_whenNotUpdatedBankAccount() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.update(expected)).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.replenishmentAccount(id, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Operation is failed. Bank account is not updated");
    }

    @Test
    public void test_replenishmentAccount_shouldThrowException_whenNotCreateTransaction() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.update(expected)).thenReturn(Optional.of(expected));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.replenishmentAccount(id, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Transaction is not created");
    }

    @Test
    public void test_replenishmentAccount_shouldThrowException_whenNotConfirmTransaction() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.update(expected)).thenReturn(Optional.of(expected));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transaction));
        Mockito.when(transactionDao.read(any())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.replenishmentAccount(id, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Transaction is not read");
    }

    @Test
    public void test_withdrawal_shouldReturnUpdatedBankAccount_whenEnterCorrectData() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        expected.setBalance(expected.getBalance().add(money));
        Mockito.when(bankAccountDao.update(expected)).thenReturn(Optional.of(expected));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transactionWithId));
        Mockito.when(transactionDao.read(id)).thenReturn(Optional.of(transactionWithId));
        BankAccount actual = bankAccountService.withdrawal(id, money);

        expected.setBalance(expected.getBalance().subtract(money));
        expected.setId(id);

        assertEquals(expected, actual);
    }

    @Test
    public void test_withdrawal_shouldThrowException_whenAbsentAccountId() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.withdrawal(id, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Not found bank account");
    }

    @Test
    public void test_withdrawal_shouldThrowException_whenAmountMoreThenBalance() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.withdrawal(id, money.add(money)));

        assertEquals(serviceException.getMessage(), "could not transfer money. Not enough funding");
    }

    @Test
    public void test_withdrawal_shouldThrowException_whenNotUpdatedBankAccount() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.update(expected)).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.withdrawal(id, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Operation is failed. Bank account is not updated");
    }

    @Test
    public void test_withdrawal_shouldThrowException_whenNotCreateTransaction() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.update(expected)).thenReturn(Optional.of(expected));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.withdrawal(id, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Transaction is not created");
    }

    @Test
    public void test_withdrawal_shouldThrowException_whenNotConfirmTransaction() {
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.update(expected)).thenReturn(Optional.of(expected));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transaction));
        Mockito.when(transactionDao.read(any())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.withdrawal(id, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Transaction is not read");
    }

    @Test
    public void test_transferMoney_shouldReturnUpdatedBankAccount_whenEnterCorrectData() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.of(bestBank));
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.readByAccountLoginAndBankId(anotherLogin, anotherId)).thenReturn(Optional.of(anotherBankAccount));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transactionWithId));
        Mockito.when(transactionDao.read(id)).thenReturn(Optional.of(transactionWithId));
        Mockito.when(bankAccountDao.update(any())).thenReturn(Optional.of(expected), Optional.of(anotherBankAccount));
        BigDecimal actual = bankAccountService.transferMoney(id, anotherId, anotherLogin, money);

        assertEquals(BigDecimal.ZERO, actual);
    }

    @Test
    public void test_transferMoney_shouldThrowException_whenAbsentBankId() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () ->
                bankAccountService.transferMoney(id, anotherId, anotherLogin, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Bank is not found");
    }

    @Test
    public void test_transferMoney_shouldThrowException_whenAbsentOwnerAccountId() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.of(bestBank));
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () ->
                bankAccountService.transferMoney(id, anotherId, anotherLogin, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Bank Account owner is not found");
    }


    @Test
    public void test_transferMoney_shouldThrowException_whenAbsentAccountLogin() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.of(bestBank));
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.readByAccountLoginAndBankId(anotherLogin, anotherId)).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () ->
                bankAccountService.transferMoney(id, anotherId, anotherLogin, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Bank Account of user is not found");
    }

    @Test
    public void test_transferMoney_shouldThrowException_whenNotCreateTransaction() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.of(bestBank));
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.readByAccountLoginAndBankId(anotherLogin, anotherId)).thenReturn(Optional.of(anotherBankAccount));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.transferMoney(id, anotherId, anotherLogin, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Transaction is not created");
    }

    @Test
    public void test_transferMoney_shouldThrowException_whenNotConfirmTransaction() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.of(bestBank));
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.readByAccountLoginAndBankId(anotherLogin, anotherId)).thenReturn(Optional.of(anotherBankAccount));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transaction));
        Mockito.when(transactionDao.read(any())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.transferMoney(id, anotherId, anotherLogin, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Transaction is not read");
    }

    @Test
    public void test_transferMoney_shouldThrowException_whenOwnerBalanceLessThenAmountForTransfer() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.of(bestBank));
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.readByAccountLoginAndBankId(anotherLogin, anotherId)).thenReturn(Optional.of(anotherBankAccount));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transactionWithId));
        Mockito.when(transactionDao.read(any())).thenReturn(Optional.of(transactionWithId));

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.transferMoney(id, anotherId, anotherLogin, money.add(money)));

        assertEquals(serviceException.getMessage(), "could not transfer money. Not enough funding");
    }

    @Test
    public void test_transferMoney_shouldThrowException_whenOwnerBankAccountNotUpdated() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.of(bestBank));
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.readByAccountLoginAndBankId(anotherLogin, anotherId)).thenReturn(Optional.of(anotherBankAccount));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transactionWithId));
        Mockito.when(transactionDao.read(any())).thenReturn(Optional.of(transactionWithId));
        Mockito.when(bankAccountDao.update(any())).thenReturn(Optional.empty(), Optional.of(anotherBankAccount));

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.transferMoney(id, anotherId, anotherLogin, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Bank account is not updated");
    }

    @Test
    public void test_transferMoney_shouldThrowException_whenTransferToBankAccountNotUpdated() {
        Mockito.when(bankDao.read(anotherId)).thenReturn(Optional.of(bestBank));
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id, id)).thenReturn(Optional.of(expected));
        Mockito.when(bankAccountDao.readByAccountLoginAndBankId(anotherLogin, anotherId)).thenReturn(Optional.of(anotherBankAccount));
        Mockito.when(transactionDao.create(any())).thenReturn(Optional.of(transactionWithId));
        Mockito.when(transactionDao.read(any())).thenReturn(Optional.of(transactionWithId));
        Mockito.when(bankAccountDao.update(any())).thenReturn(Optional.of(expected), Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.transferMoney(id, anotherId, anotherLogin, money));

        assertEquals(serviceException.getMessage(), "could not transfer money. Bank account is not updated");
    }

    @Test
    public void test_accruePercentOnUserBalancesOfCleverBank_shouldRun_whenEnterCorrectData() {
        List<BankAccount> bankAccounts = List.of(bankAccount);
        Mockito.when(bankAccountDao.readByBankId(id)).thenReturn(bankAccounts);
        Mockito.when(bankAccountDao.update(any())).thenReturn(Optional.of(expected));

        bankAccountService.accruePercentOnUserBalancesOfCleverBank();

        verify(bankAccountDao).update(bankAccount);
    }

    @Test
    public void test_accruePercentOnUserBalancesOfCleverBank_shouldThrowException_whenBankAccountsAbsent() {
        Mockito.when(bankAccountDao.readByBankId(id)).thenReturn(Collections.emptyList());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> bankAccountService.accruePercentOnUserBalancesOfCleverBank());

        assertEquals(serviceException.getMessage(), "Empty");
    }

    @Test
    public void test_accruePercentOnUserBalancesOfCleverBank_shouldThrowException_whenBankAccountNotUpdate() {
        List<BankAccount> bankAccounts = List.of(expected);
        Mockito.when(bankAccountDao.readByBankId(id)).thenReturn(bankAccounts);
        Mockito.when(bankAccountDao.update(any())).thenReturn(Optional.empty());

        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> bankAccountService.accruePercentOnUserBalancesOfCleverBank());

        assertEquals(notFoundEntityException.getMessage(), "Bank account with id=1 is not updated");
    }

}
