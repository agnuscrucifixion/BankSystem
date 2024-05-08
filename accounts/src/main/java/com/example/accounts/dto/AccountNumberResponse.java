package com.example.accounts.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class AccountNumberResponse {
    private String currency;
    private BigDecimal amount;
}
