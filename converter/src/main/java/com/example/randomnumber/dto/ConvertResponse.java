package com.example.randomnumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class ConvertResponse {
    private String currency;
    private BigDecimal amount;

}
