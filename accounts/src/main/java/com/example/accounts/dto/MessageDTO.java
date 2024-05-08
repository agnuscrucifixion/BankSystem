package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MessageDTO {

    private Integer customerId;

    private String message;
}
