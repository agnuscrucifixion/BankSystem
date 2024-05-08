package com.example.accounts.entities;

import com.example.accounts.dto.FeeDTO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "fees")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fee")
    double fee;

    public Fee() {
    }

    public Fee(FeeDTO feeDTO) {
        this.fee = feeDTO.getFee();
    }
}
