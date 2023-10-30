package org.example.clever_bank.service;

import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.dao.impl.TransactionDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.impl.TransactionServiceImpl;
import org.example.clever_bank.service.text.PaperWorker;
import org.example.clever_bank.util.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;
    @Mock
    private TransactionDaoImpl transactionDao;
    @Mock
    private BankAccountDaoImpl bankAccountDao;
    @Mock
    private AccountDaoImpl accountDao;
    @Mock
    private PaperWorker paperWorker;
    @Spy
    private ConnectionPool connectionPool;

    private BankAccount bankAccountFrom;
    private BankAccount bankAccountTo;
    private Account accountFrom;
    private Account accountTo;
    private Long id;
    private String login;
    private String password;
    private Long secondId;
    private String secondLogin;
    private String secondPassword;
    private BigDecimal money;
    private Transaction transaction;
    private Transaction expected;
    private LocalDateTime dateTime;
    private LocalDateTime dateCreateBankAccountTo;


    @BeforeEach
    public void init() {
        ConnectionPool.lockingPool().init();

        try (MockedStatic<ConfigurationManager> manager = Mockito.mockStatic(ConfigurationManager.class)) {
            manager.when(() -> ConfigurationManager.getProperty("bank.id")).thenReturn("id");
        }

        id = 1l;
        login = "Semenovich";
        password = "semen";
        secondId = 2L;
        secondLogin = "Groot";
        secondPassword = "grooty";
        money = BigDecimal.valueOf(1000);
        dateTime = LocalDateTime.of(2023, 10, 20, 15, 30);
        LocalDateTime dateCreateBankAccountFrom = LocalDateTime.of(2023, 06, 20, 15, 30);
        dateCreateBankAccountTo = LocalDateTime.of(2023, 01, 20, 15, 30);

        accountFrom = Account.builder()
                .id(id)
                .login(login)
                .password(password)
                .build();

        accountTo = Account.builder()
                .id(secondId)
                .login(secondLogin)
                .password(secondPassword)
                .build();

        Bank cleverBank = Bank.builder()
                .id(id)
                .name("clever_bank")
                .build();

        Bank bestBank = Bank.builder()
                .id(secondId)
                .name("best_bank")
                .build();

        bankAccountFrom = BankAccount.builder()
                .id(id)
                .account(accountFrom)
                .banks(List.of(cleverBank))
                .balance(money)
                .dateCreate(dateCreateBankAccountFrom)
                .build();

        bankAccountTo = BankAccount.builder()
                .id(secondId)
                .account(accountTo)
                .banks(List.of(bestBank))
                .balance(money)
                .dateCreate(dateCreateBankAccountTo)
                .build();

        transaction = Transaction.builder()
                                    .bankAccountFrom(bankAccountFrom)
                                    .bankAccountTo(bankAccountTo)
                                    .sum(money)
                                    .type("Transfer")
                                    .build();

        expected = Transaction.builder()
                                .id(id)
                                .bankAccountFrom(bankAccountFrom)
                                .bankAccountTo(bankAccountTo)
                                .sum(money)
                                .type("Transfer")
                                .dateCreate(dateTime)
                                .build();
    }

    @Test
    public void test_add_shouldReturnTransaction_whenEnterCorrectData() throws ValidationException {
        Mockito.when(bankAccountDao.read(transaction.getBankAccountFrom().getId())).thenReturn(Optional.of(bankAccountFrom));
        Mockito.when(bankAccountDao.read(transaction.getBankAccountTo().getId())).thenReturn(Optional.of(bankAccountTo));
        Mockito.when(transactionDao.create(transaction)).thenReturn(Optional.of(expected));

        Transaction actual = transactionService.add(transaction);

        assertEquals(expected, actual);
        verify(transactionDao).create(transaction);
    }

    @Test
    public void test_add_shouldThrowException_whenEnterIncorrectType() {
        transaction.setType("bb");

        ValidationException validationException = assertThrows(ValidationException.class, () -> transactionService.add(transaction));

        assertEquals(validationException.getMessage(), "Transaction type is not valid");
    }

    @Test
    public void test_add_shouldThrowException_whenBankAccountFromAbsent() {
        Mockito.when(bankAccountDao.read(transaction.getBankAccountFrom().getId())).thenReturn(Optional.empty());

        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> transactionService.add(transaction));
        assertEquals(notFoundEntityException.getMessage(), String.format("Bank account with id=%d is not present", bankAccountFrom.getId()));
    }

    @Test
    public void test_add_shouldThrowException_whenBankAccountToAbsent() {
        Mockito.when(bankAccountDao.read(transaction.getBankAccountFrom().getId())).thenReturn(Optional.of(bankAccountFrom));
        Mockito.when(bankAccountDao.read(transaction.getBankAccountTo().getId())).thenReturn(Optional.empty());

        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> transactionService.add(transaction));
        assertEquals(notFoundEntityException.getMessage(), String.format("Bank account with id=%d is not present", bankAccountTo.getId()));
    }

    @Test
    public void test_add_shouldThrowException_whenTransactionNotCreate() {
        Mockito.when(bankAccountDao.read(transaction.getBankAccountFrom().getId())).thenReturn(Optional.of(bankAccountFrom));
        Mockito.when(bankAccountDao.read(transaction.getBankAccountTo().getId())).thenReturn(Optional.of(bankAccountTo));
        Mockito.when(transactionDao.create(transaction)).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> transactionService.add(transaction));

        assertEquals(serviceException.getMessage(), "Transaction is not created");
    }

    @Test
    public void test_findById_shouldReturnTransaction_whenEnterCorrectId() throws ValidationException {
        Mockito.when(transactionDao.read(expected.getId())).thenReturn(Optional.of(expected));

        Transaction actual = transactionService.findById(expected.getId());

        assertEquals(expected, actual);
        verify(transactionDao).read(id);
    }

    @Test
    public void test_findById_shouldThrowException_whenEnterIncorrectId() {
        Mockito.when(transactionDao.read(expected.getId())).thenReturn(Optional.empty());

        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> transactionService.findById(id));
        assertEquals(notFoundEntityException.getMessage(), String.format("Transaction with id=%d is not found", id));
    }

    @Test
    public void test_findAll_shouldReturnAllTransactions() {
        List<Transaction> expectedList = List.of(expected);
        Mockito.when(transactionDao.readAll()).thenReturn(expectedList);

        List<Transaction> actual = transactionService.findAll();

        assertEquals(expectedList, actual);
        verify(transactionDao).readAll();
    }

    @Test
    public void test_findAll_shouldThrowException_whenEmpty() {
        Mockito.when(transactionDao.readAll()).thenReturn(Collections.emptyList());

        ServiceException exception = assertThrows(ServiceException.class, () -> transactionService.findAll());
        assertEquals(exception.getMessage(), "Empty");
    }

    @Test
    public void test_update_shouldThrowException_whenEnterCorrectData() throws ValidationException {
        Mockito.when(transactionDao.update(transaction)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> transactionService.update(transaction));
        assertEquals(exception.getMessage(), "Transaction is not updated");
    }

    @Test
    public void test_remove_shouldThrowException_whenTransactionNotFound() {
        Mockito.when(transactionDao.read(expected.getId())).thenReturn(Optional.empty());

        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> transactionService.remove(id));
        assertEquals(exception.getMessage(), String.format("Transaction with id=%d is not found", id));
    }

    @Test
    public void test_remove_shouldThrowException_whenTransactionNotUpdate() {
        Mockito.when(transactionDao.read(expected.getId())).thenReturn(Optional.of(expected));
        Mockito.when(transactionDao.delete(expected.getId())).thenReturn(false);

        assertFalse(transactionService.remove(id));
        verify(transactionDao).delete(id);
    }

    @Test
    public void test_createStatementOfAccount_shouldReturnText_whenPeriodFromEqualsPeriodTo() throws IOException, URISyntaxException {
        LocalDateTime periodFrom = LocalDateTime.of(2023, 10, 20, 15, 30);
        LocalDateTime periodTo = periodFrom;
        String fileType = "txt";
        String expectedText = "ExpectedText";

        List<Transaction> expectedList = List.of(expected);

        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id,id)).thenReturn(Optional.of(bankAccountTo));
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(accountTo));
        Mockito.when(transactionDao.readByPeriodAndAccountId(id, dateCreateBankAccountTo, periodTo)).thenReturn(expectedList);
        Mockito.when(paperWorker.createStatement(expectedList, dateCreateBankAccountTo, periodTo, fileType)).thenReturn(expectedText);

        String actual = transactionService.createStatementOfAccount(id, periodFrom, periodTo, fileType);

        assertEquals(expectedText, actual);
        verify(transactionDao).readByPeriodAndAccountId(id, dateCreateBankAccountTo, periodTo);
    }

    @Test
    public void test_createStatementOfAccount_shouldReturnText_whenPeriodFromNotEqualsPeriodTo() throws IOException, URISyntaxException {
        LocalDateTime periodFrom = LocalDateTime.of(2023, 10, 20, 15, 30);
        LocalDateTime periodTo = LocalDateTime.of(2023, 10, 25, 15, 30);
        String fileType = "txt";
        String expectedText = "ExpectedText";

        List<Transaction> expectedList = List.of(expected);

        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(accountTo));
        Mockito.when(transactionDao.readByPeriodAndAccountId(id, periodFrom, periodTo)).thenReturn(expectedList);
        Mockito.when(paperWorker.createStatement(expectedList, periodFrom, periodTo, fileType)).thenReturn(expectedText);

        String actual = transactionService.createStatementOfAccount(id, periodFrom, periodTo, fileType);

        assertEquals(expectedText, actual);
        verify(transactionDao).readByPeriodAndAccountId(id, periodFrom, periodTo);
    }

    @Test
    public void test_createStatementOfAccount_shouldThrowException_whenBankAccountNotFound() {
        LocalDateTime periodFrom = LocalDateTime.of(2023, 10, 20, 15, 30);
        LocalDateTime periodTo = periodFrom;
        String fileType = "txt";
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id,id)).thenReturn(Optional.empty());

        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () ->
                transactionService.createStatementOfAccount(id, periodFrom, periodTo, fileType));
        assertEquals(exception.getMessage(), String.format("Bank account with account id=%d is not found", id));
    }

    @Test
    public void test_createStatementOfAccount_shouldThrowException_whenAccountNotFound() {
        LocalDateTime periodFrom = LocalDateTime.of(2023, 10, 20, 15, 30);
        LocalDateTime periodTo = periodFrom;
        String fileType = "txt";
        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id,id)).thenReturn(Optional.of(bankAccountTo));
        Mockito.when(accountDao.read(id)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () ->
                transactionService.createStatementOfAccount(id, periodFrom, periodTo, fileType));
        assertEquals(exception.getMessage(), "could not transfer money. Account is not found");
    }

    @Test
    public void test_createStatementOfAccount_shouldThrowException_whenTransactionsAbsent() {
        LocalDateTime periodFrom = LocalDateTime.of(2023, 10, 20, 15, 30);
        LocalDateTime periodTo = periodFrom;
        String fileType = "txt";

        Mockito.when(bankAccountDao.readByAccountIdAndBankId(id,id)).thenReturn(Optional.of(bankAccountTo));
        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(accountTo));
        Mockito.when(transactionDao.readByPeriodAndAccountId(id, dateCreateBankAccountTo, periodTo)).thenReturn(Collections.emptyList());

        ServiceException exception = assertThrows(ServiceException.class, () ->
                transactionService.createStatementOfAccount(id, periodFrom, periodTo, fileType));
        assertEquals(exception.getMessage(), "Transactions are absent");
    }
}
