package com.example.accounts.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Data
public class TransferDTO {

    Integer receiverAccount;
    Integer senderAccount;
    BigDecimal amountInSenderCurrency;
}
