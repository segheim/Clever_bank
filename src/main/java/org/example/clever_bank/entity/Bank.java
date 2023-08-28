package org.example.clever_bank.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Bank implements Entity {

    private Long id;
    private String name;
    private List<BankAccount> bankAccounts;

}
