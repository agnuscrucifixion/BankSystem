package com.example.accounts.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "transactions")
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "amount")
    private String amount;

    @Column(name = "account_number")
    private int accountNumber;

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", accountNumber=" + accountNumber +
                '}';
    }
}
