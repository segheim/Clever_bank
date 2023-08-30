package org.example.clever_bank.service.impl;

import org.example.clever_bank.dao.impl.BankDaoImpl;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.exception.NotFoundEntityException;
import org.example.clever_bank.exception.ServiceException;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.BankService;
import org.example.clever_bank.validation.Validator;

import java.util.List;

public class BankServiceImpl implements BankService {

    private final BankDaoImpl bankDao;

    private BankServiceImpl(BankDaoImpl bankDao) {
        this.bankDao = bankDao;
    }

    @Override
    public Bank add(Bank bank) throws ValidationException {
        if (Validator.getInstance().validateLogin(bank.getName())) {
            throw new ValidationException("Bank name is not valid");
        }
        bankDao.readByName(bank.getName()).orElseThrow(() -> new NotFoundEntityException(String.format("Account with name=%s already exist", bank.getName())));
        return bankDao.create(bank).orElseThrow(() -> new ServiceException("Bank is not created"));
    }

    @Override
    public Bank findById(Long id) {
        return bankDao.read(id).orElseThrow(() -> new ServiceException("Bank is not created"));
    }

    @Override
    public List<Bank> findAll() {
        List<Bank> banks = bankDao.readAll();
        if (banks.isEmpty()) {
            throw new ServiceException("Empty");
        }
        return banks;
    }

    @Override
    public Bank update(Bank bank) throws ValidationException {
        if (Validator.getInstance().validateLogin(bank.getName())) {
            throw new ValidationException("Bank name is not valid");
        }
        bankDao.read(bank.getId()).orElseThrow(() -> new NotFoundEntityException(String.format("Bank with id=%d is not found", bank.getId())));
        return bankDao.update(bank).orElseThrow(() -> new ServiceException("Bank is not updated"));
    }

    @Override
    public boolean remove(Long id) {
        bankDao.read(id).orElseThrow(() -> new NotFoundEntityException(String.format("Bank with id=%d is not found", id)));
        return bankDao.delete(id);
    }

    public static BankServiceImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final BankServiceImpl INSTANCE = new BankServiceImpl(BankDaoImpl.getInstance());
    }
}
