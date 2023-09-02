package org.example.clever_bank.service;

import org.example.clever_bank.dao.AccountDao;
import org.example.clever_bank.dao.BankAccountDao;
import org.example.clever_bank.dao.BankDao;
import org.example.clever_bank.dao.TransactionDao;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.impl.AccountServiceImpl;
import org.example.clever_bank.service.impl.BankAccountServiceImpl;
import org.example.clever_bank.service.text.PaperWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

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
    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private BankAccount bankAccount;
    private BankAccount expected;
    private Account account;
    private Long id;
    private String login;
    private String password;

    @BeforeEach
    public void init() {

        id = 1l;
        login = "Semenovich";
        password = "semen";
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
                        .balance(BigDecimal.valueOf(1000))
                        .build();
        expected = BankAccount.builder()
                .id(id)
                .account(account)
                .banks(List.of(Bank.builder()
                        .id(id)
                        .name("clever_bank")
                        .build()))
                .balance(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    public void test_add_shouldEnterBankAccountToBankAccountDao_whenEnterCorrectData() {

//        Mockito.when(accountDao.read(id)).thenReturn(Optional.of(account));
//        Mockito.when(bankAccountDao.create(bankAccount)).thenReturn(Optional.of(expected));
//        Mockito.when(bankAccountDao.createBankBankAccount(id, id)).thenReturn(true);
//
//        BankAccount actual = bankAccountService.add(bankAccount);
//
//        assertEquals(expected, actual);
//        verify(accountDao).create(account);
    }
}
