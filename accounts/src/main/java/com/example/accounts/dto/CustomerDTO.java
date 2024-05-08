package com.example.accounts.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class CustomerDTO {

    private String firstName;

    private String lastName;

    private LocalDate birthDay;


}
