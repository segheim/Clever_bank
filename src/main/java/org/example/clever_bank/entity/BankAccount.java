package org.example.clever_bank.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BankAccount implements Entity{

    private Long id;
    private List<Bank> bank;
    private Account account;
    private BigDecimal balance;

}
