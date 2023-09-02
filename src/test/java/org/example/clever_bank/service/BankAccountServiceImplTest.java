package org.example.clever_bank.service;

import org.example.clever_bank.dao.AccountDao;
import org.example.clever_bank.dao.BankAccountDao;
import org.example.clever_bank.dao.BankDao;
import org.example.clever_bank.dao.TransactionDao;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.impl.AccountServiceImpl;
import org.example.clever_bank.service.text.PaperWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private AccountServiceImpl accountService;



    private Account account;
    private Account expected;
    private Long id;
    private String login;
    private String password;

    @BeforeEach
    public void init() {

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
    }

    @Test
    public void test_add_shouldEnterAccountToAccountDao_whenEnterCorrectData() throws ValidationException {

        Mockito.when(accountDao.create(account)).thenReturn(Optional.of(expected));
        Mockito.when(accountDao.readByLogin(login)).thenReturn(Optional.empty());
        Account actual = accountService.add(account);

        assertEquals(expected, actual);
        verify(accountDao).create(account);
    }
}
