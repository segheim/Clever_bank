package org.example.clever_bank.dao;

import org.example.clever_bank.entity.Bank;

import java.util.Optional;

public interface BankDao {

    Optional<Bank> readByName(String name);
}
