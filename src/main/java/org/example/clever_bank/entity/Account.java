package org.example.clever_bank.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Account implements Entity{

    private Long id;
    private String login;
    private String password;

}
