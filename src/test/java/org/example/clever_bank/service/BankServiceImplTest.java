package org.example.clever_bank.service;

import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.BankDaoImpl;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.impl.BankServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BankServiceImplTest {

    @InjectMocks
    private BankServiceImpl bankService;
    @Mock
    private BankDaoImpl bankDao;
    @Spy
    private ConnectionPool connectionPool;

    private Long id;
    private Bank bank;
    private Bank expected;
    private String bankName;

    @BeforeEach
    public void init() {
        ConnectionPool.lockingPool().init();
        id = 1l;
        bankName = "clever_bank";

        bank = Bank.builder()
                .name(bankName)
                .build();

        expected = Bank.builder()
                        .id(id)
                        .name(bankName)
                        .build();
    }

    @Test
    public void test_add_shouldReturnBank_whenEnterCorrectData() throws ValidationException {
        Mockito.when(bankDao.readByName(bankName)).thenReturn(Optional.empty());
        Mockito.when(bankDao.create(bank)).thenReturn(Optional.of(expected));

        Bank actual = bankService.add(bank);

        assertEquals(expected, actual);
        verify(bankDao).create(bank);
    }

    @Test
    public void test_add_shouldThrowException_whenEnterIncorrectBankName() {
        bank.setName("bb");

        ValidationException validationException = assertThrows(ValidationException.class, () -> bankService.add(bank));

        assertEquals(validationException.getMessage(), "Bank name is not valid");
    }

    @Test
    public void test_add_shouldThrowException_whenBankPresent() {
        Mockito.when(bankDao.readByName(bankName)).thenReturn(Optional.of(expected));

        ServiceException exception = assertThrows(ServiceException.class, () -> bankService.add(bank));

        assertEquals(exception.getMessage(), String.format("Bank with name=%s is present", bank.getName()));
    }

    @Test
    public void test_add_shouldThrowException_whenBankNotCreate() {
        Mockito.when(bankDao.readByName(bankName)).thenReturn(Optional.empty());
        Mockito.when(bankDao.create(bank)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bankService.add(bank));

        assertEquals(exception.getMessage(), "Bank is not created");
    }
}
