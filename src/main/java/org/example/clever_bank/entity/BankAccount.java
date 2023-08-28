package org.example.clever_bank.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class BankAccount implements Entity{

    private Long id;
    private List<Account> accounts;
    private BigDecimal balance;

}
