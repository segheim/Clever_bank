package org.example.clever_bank.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BankAccount implements Entity {

    private Long id;
    private Account account;
    private List<Bank> banks;
    private BigDecimal balance;
    private LocalDateTime dateCreate;

}
