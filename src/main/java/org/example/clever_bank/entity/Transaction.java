package org.example.clever_bank.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class Transaction implements Entity {

    private Long id;
    private BankAccount bankAccountFrom;
    private BankAccount bankAccountTo;
    private BigDecimal sum;

}
