package com.example.accounts.services;

import com.example.accounts.config.AccountsConfig;

import java.math.RoundingMode;
import java.util.UUID;
import com.example.accounts.config.AccountsConfigEnv;
import com.example.accounts.dto.*;
import com.example.accounts.entities.Account;
import com.example.accounts.entities.Customer;
import com.example.accounts.entities.Message;
import com.example.accounts.entities.Transaction;
import com.example.accounts.repositories.AccountsRepository;
import com.example.accounts.repositories.CustomerRepository;
import com.example.accounts.repositories.OutboxMessageRepository;
import com.example.accounts.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AccountsService {
    private final AccountsRepository accountsRepository;
    private final AccountsConfigEnv accountsConfigEnv;

    private final OutboxService outboxService;
    private final RedisService redisService;
    private final TransactionService transactionService;
    private final SendSocketService sendSocketService;
    @Autowired
    public AccountsService(AccountsRepository accountsRepository, AccountsConfigEnv accountsConfigEnv,
                           OutboxService outboxService, RedisService redisService,
                           TransactionService transactionService, SendSocketService sendSocketService) {
        this.accountsRepository = accountsRepository;
        this.accountsConfigEnv = accountsConfigEnv;
        this.outboxService = outboxService;
        this.redisService = redisService;
        this.transactionService = transactionService;
        this.sendSocketService = sendSocketService;
    }
    int accNum = 1;
    private Integer createAccountNumber() {
        while (accountsRepository.findAccountByAccountNumber(accNum).isPresent()) {
            accNum++;
        }
        return accNum;
    }
    public ResponseEntity<CreateAccountResponse> createCustomerAccount(AccountDTO accountDTO) {
        if (accountDTO.getCustomerId() == null || accountDTO.getCustomerId().toString().isEmpty() || accountDTO.getCurrency() == null
                || accountDTO.getCurrency().isEmpty() || !accountDTO.getCustomerId().toString().matches("\\d+")
                || !accountsConfigEnv.getCurrencies().contains(accountDTO.getCurrency())) {
            return ResponseEntity.badRequest().build();
        }
        if (accountsRepository.findAccountByCustomerId(accountDTO.getCustomerId()).stream().anyMatch(x -> x.getCurrency().equals(accountDTO.getCurrency()))) {
            return ResponseEntity.badRequest().build();
        }
        Account account = new Account();
        account.setAccountNumber(createAccountNumber());
        account.setCurrency(accountDTO.getCurrency());
        account.setBalance(BigDecimal.ZERO);
        account.setCustomerId(accountDTO.getCustomerId());
        CreateAccountResponse createAccountResponse = new CreateAccountResponse();
        createAccountResponse.setAccountNumber(account.getAccountNumber());
        accountsRepository.save(account);
        sendSocketService.sendToSocket(account);
        return ResponseEntity.status(HttpStatus.OK).body(createAccountResponse);
    }

    public ResponseEntity<AccountNumberResponse> findByAccNumber(Integer accNum) {
        if (accNum == null || accNum.toString().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AccountNumberResponse accountNumberResponse = new AccountNumberResponse();
        var acc = accountsRepository.findAccountByAccountNumber(accNum);
        if (acc.isPresent()) {
            accountNumberResponse.setAmount(acc.get().getBalance());
            accountNumberResponse.setCurrency(acc.get().getCurrency());
        } else {
            return ResponseEntity.badRequest().build();
        }
        if (accountNumberResponse.getCurrency().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(accountNumberResponse);
    }
    public ResponseEntity<TransactionDTO> topUpAccount(String key, Integer accNum, AmountDTO amountDTO) {
        if (accNum == null || accNum.toString().isEmpty() ||  accNum == 0) {
            return ResponseEntity.badRequest().build();
        }
        if (amountDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (key != null) {
            if (redisService.contain(key)) {
                return ResponseEntity.ok(redisService.getCache(key));
            }
        }
        Optional<Account> acc = accountsRepository.findAccountByAccountNumber(accNum);
        if (acc.isPresent()) {
            Account getAcc = acc.get();
            getAcc.setBalance(getAcc.getBalance().add(amountDTO.getAmount()));
            accountsRepository.save(getAcc);
            sendSocketService.sendToSocket(getAcc);

            outboxService.save(getAcc, amountDTO.getAmount());


            TransactionDTO transaction = new TransactionDTO();
            transaction.setTransactionId(String.valueOf(UUID.randomUUID()));
            transaction.setAmount(String.valueOf(amountDTO.getAmount()));

            if (key != null) {
                redisService.saveCache(key, transaction);
            }

            transactionService.save(transaction, accNum, amountDTO.getAmount());
            return ResponseEntity.ok(transaction);
        }
        return ResponseEntity.badRequest().build();
    }

}
