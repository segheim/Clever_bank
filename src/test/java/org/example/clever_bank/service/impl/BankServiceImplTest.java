package org.example.clever_bank.service.impl;

import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.BankDaoImpl;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.exception.NotFoundEntityException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void test_findById_shouldReturnBank_whenEnterCorrectId() {
        Mockito.when(bankDao.read(id)).thenReturn(Optional.of(expected));

        Bank actual = bankService.findById(id);

        assertEquals(expected, actual);
        verify(bankDao).read(id);
    }

    @Test
    public void test_findById_shouldReturnBank_whenEnterIncorrectId() {
        Mockito.when(bankDao.read(id)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bankService.findById(id));

        assertEquals(exception.getMessage(), "Bank is not created");
    }

    @Test
    public void test_findAll_shouldReturnAllBanks() {
        List<Bank> expectedList = List.of(expected);
        Mockito.when(bankDao.readAll()).thenReturn(expectedList);

        List<Bank> actual = bankService.findAll();

        assertEquals(expectedList, actual);
        verify(bankDao).readAll();
    }

    @Test
    public void test_findAll_shouldThrowException_whenEmpty() {
        Mockito.when(bankDao.readAll()).thenReturn(Collections.emptyList());

        ServiceException exception = assertThrows(ServiceException.class, () -> bankService.findAll());

        assertEquals(exception.getMessage(), "Empty");
    }

    @Test
    public void test_update_shouldReturnBank_whenEnterCorrectData() throws ValidationException {
        Mockito.when(bankDao.read(id)).thenReturn(Optional.of(expected));
        Mockito.when(bankDao.update(expected)).thenReturn(Optional.of(expected));

        Bank actual = bankService.update(expected);

        assertEquals(expected, actual);
        verify(bankDao).update(expected);
    }

    @Test
    public void test_update_shouldThrowException_whenEnterIncorrectBankName() {
        bank.setName("bb");

        ValidationException validationException = assertThrows(ValidationException.class, () -> bankService.update(bank));
        assertEquals(validationException.getMessage(), "Bank name is not valid");
    }

    @Test
    public void test_update_shouldThrowException_whenBankNotFound() {
        Mockito.when(bankDao.read(expected.getId())).thenReturn(Optional.empty());

        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> bankService.update(expected));
        assertEquals(exception.getMessage(), String.format("Bank with id=%d is not found", id));
    }

    @Test
    public void test_update_shouldThrowException_whenBankNotUpdate() {
        Mockito.when(bankDao.read(expected.getId())).thenReturn(Optional.of(expected));
        Mockito.when(bankDao.update(expected)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bankService.update(expected));
        assertEquals(exception.getMessage(), "Bank is not updated");
    }

    @Test
    public void test_remove_shouldReturnBank_whenEnterCorrectId() throws ValidationException {
        Mockito.when(bankDao.read(id)).thenReturn(Optional.of(expected));
        Mockito.when(bankDao.delete(id)).thenReturn(true);

        assertTrue(bankService.remove(id));
        verify(bankDao).delete(id);
    }

    @Test
    public void test_remove_shouldThrowException_whenBankNotFound() {
        Mockito.when(bankDao.read(expected.getId())).thenReturn(Optional.empty());

        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> bankService.remove(id));
        assertEquals(exception.getMessage(), String.format("Bank with id=%d is not found", id));
    }

    @Test
    public void test_remove_shouldThrowException_whenBankNotRemove() {
        Mockito.when(bankDao.read(expected.getId())).thenReturn(Optional.of(expected));
        Mockito.when(bankDao.delete(expected.getId())).thenReturn(false);

        assertFalse(bankService.remove(id));
        verify(bankDao).delete(id);
    }
}
