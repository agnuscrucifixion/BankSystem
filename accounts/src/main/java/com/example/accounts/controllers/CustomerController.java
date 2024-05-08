package com.example.accounts.controllers;


import com.example.accounts.dto.*;
import com.example.accounts.exceptions.GetRateLimitException;
import com.example.accounts.services.BucketService;
import com.example.accounts.services.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
public class CustomerController {
    private final CustomerService customerService;
    private final BucketService bucketService;
    @Autowired
    public CustomerController(CustomerService customerService, BucketService bucketService) {
        this.customerService = customerService;
        this.bucketService = bucketService;
    }
    @PostMapping("/customers")
    ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.createCustomer(customerDTO);
    }

    @GetMapping("/customers/{customerId}/balance")
    public ResponseEntity<CustomerAllBalanceResponse> showBalance(@PathVariable(value = "customerId") Integer id,
                                                                  @RequestParam(value = "currency") String currency) {
        Bucket bucket = bucketService.get(id);
        if (!bucket.tryConsume(1)) {
            throw new GetRateLimitException("Лимит на взяите курса");
        }
        return customerService.showBalance(id, currency);
    }

    @Transactional
    @PostMapping("/transfers")
    public ResponseEntity<TransactionDTO> transferMoney(@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey, @RequestBody TransferDTO transferDTO) {
        return customerService.transferMoney(idempotencyKey, transferDTO);
    }
}
