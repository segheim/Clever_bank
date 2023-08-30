package org.example.clever_bank.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BankAccount implements Entity{

    private Long id;
    private Account account;
    private BigDecimal balance;
    private LocalDateTime dateCreate;

}
