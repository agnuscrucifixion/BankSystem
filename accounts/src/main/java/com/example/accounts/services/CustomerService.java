package com.example.accounts.services;

import com.example.accounts.config.AccountsConfigEnv;
import com.example.accounts.config.FeeConfig;
import com.example.accounts.config.KeycloakConfig;
import com.example.accounts.dto.*;
import com.example.accounts.entities.*;
import com.example.accounts.exceptions.GrpcException;
import com.example.accounts.repositories.AccountsRepository;
import com.example.accounts.repositories.CustomerRepository;
import com.example.accounts.repositories.OutboxMessageRepository;
import com.example.accounts.repositories.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountsRepository accountsRepository;
    private final AccountsConfigEnv accountsConfigEnv;
    private final KeycloakConfig keycloakConfig;
    private final RestTemplate restTemplate;
    private final ConverterControllerGrpcClient client;
    private final OutboxService outboxService;
    private final RedisService redisService;
    private final TransactionService transactionService;
    private final SendSocketService sendSocketService;
    private final KafkaSenderService kafkaSenderService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, AccountsRepository accountsRepository,
                           AccountsConfigEnv accountsConfigEnv, KeycloakConfig keycloakConfig,
                           RestTemplate restTemplate, SendSocketService sendSocketService,
                           ConverterControllerGrpcClient client, OutboxService outboxService,
                           RedisService redisService, TransactionService transactionService,
                           KafkaSenderService kafkaSenderService) {
        this.customerRepository = customerRepository;
        this.accountsRepository = accountsRepository;
        this.accountsConfigEnv = accountsConfigEnv;
        this.keycloakConfig = keycloakConfig;
        this.restTemplate = restTemplate;
        this.restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        this.client = client;
        this.outboxService = outboxService;
        this.redisService = redisService;
        this.transactionService = transactionService;
        this.sendSocketService = sendSocketService;
        this.kafkaSenderService = kafkaSenderService;
    }
    private static final Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public ResponseEntity<CreateCustomerResponse> createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getFirstName() == null || customerDTO.getFirstName().isEmpty() || customerDTO.getLastName() == null
                || customerDTO.getLastName().isEmpty() || customerDTO.getBirthDay() == null || customerDTO.getBirthDay().toString().isEmpty()
                || !pattern.matcher(customerDTO.getBirthDay().toString()).matches()) {
            return ResponseEntity.badRequest().build();
        }
        if (!LocalDate.parse(customerDTO.getBirthDay().toString()).isBefore(LocalDate.now()) || Period.between(customerDTO.getBirthDay(),
                LocalDate.now()).getYears() < 14 || Period.between(customerDTO.getBirthDay(),
                LocalDate.now()).getYears() > 120) {
            return ResponseEntity.badRequest().build();
        }
        Customer temp = new Customer();
        temp.setFirstName(customerDTO.getFirstName());
        temp.setLastName(customerDTO.getLastName());
        temp.setBirthday(customerDTO.getBirthDay());
        try {
            temp = customerRepository.save(temp);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        if (temp.getId() == 0) {
            return ResponseEntity.internalServerError().build();
        }
        CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
        createCustomerResponse.setCustomerId(temp.getId());
        return ResponseEntity.status(HttpStatus.OK).body(createCustomerResponse);
    }

    public ResponseEntity<CustomerAllBalanceResponse> showBalance(Integer id, String currency) {
        if (validateInput(id, currency)) {
            return ResponseEntity.badRequest().build();
        }
        List<Account> accounts = findCustomerAccounts(id);
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (Account account : accounts) {
            BigDecimal balanceInCurrency = convertBalance(account.getCurrency(), currency, account.getBalance());
            totalBalance = totalBalance.add(balanceInCurrency);
        }
        CustomerAllBalanceResponse response = new CustomerAllBalanceResponse();
        response.setCurrency(currency);
        response.setBalance(totalBalance);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private boolean validateInput(Integer id, String currency) {
        return String.valueOf(id).isEmpty() && currency == null && currency.isEmpty() &&
                !String.valueOf(id).matches("\\d+") && !accountsConfigEnv.getCurrencies().contains(currency);
    }

    private List<Account> findCustomerAccounts(Integer id) {
        return accountsRepository.findAccountByCustomerId(id);
    }
    private BigDecimal convertBalance(String fromCurrency, String toCurrency, BigDecimal amount) {
        converter.ConvertResponse convertResponse;
        try {
            convertResponse = client.convert(fromCurrency, toCurrency, amount);
        } catch (Exception e) {
            throw new GrpcException("Ошибка при конвертации");
        }
        if (convertResponse == null) {
            throw new GrpcException("Ответ null");
        }
        return BigDecimal.valueOf(convertResponse.getAmount());
    }

    public ResponseEntity<TransactionDTO> transferMoney(String key, TransferDTO transferDTO) {
        if (!validateTransferDTO(transferDTO)) {
            return ResponseEntity.badRequest().build();
        }
        int senderAcc = parseAccountNumber(String.valueOf(transferDTO.getSenderAccount()));
        int receiverAcc = parseAccountNumber(String.valueOf(transferDTO.getReceiverAccount()));
        if (key != null) {
            if (redisService.contain(key)) {
                return ResponseEntity.ok(redisService.getCache(key));
            }
        }
        BigDecimal amount = transferDTO.getAmountInSenderCurrency();
        Optional<Account> optSender = findAccountByAccountNumber(senderAcc);
        Optional<Account> optReceiver = findAccountByAccountNumber(receiverAcc);
        if (optSender.isEmpty() || optReceiver.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        Account sender = optSender.get();
        Account receiver = optReceiver.get();

        TransactionDTO transactionSender = performTransfer(sender, receiver, amount);
        transactionSender.setTransactionId(String.valueOf(UUID.randomUUID()));

        if (key != null) {
            redisService.saveCache(key, transactionSender);
        }

        transactionService.save(transactionSender, senderAcc, amount);
        return ResponseEntity.ok(transactionSender);
    }

    private boolean validateTransferDTO(TransferDTO transferDTO) {
        return transferDTO != null &&
                transferDTO.getSenderAccount() != null &&
                transferDTO.getReceiverAccount() != null &&
                transferDTO.getAmountInSenderCurrency() != null &&
                !transferDTO.getSenderAccount().toString().isEmpty() &&
                !transferDTO.getReceiverAccount().toString().isEmpty() &&
                isPositiveNumber(String.valueOf(transferDTO.getSenderAccount())) &&
                isPositiveNumber(String.valueOf(transferDTO.getReceiverAccount()));
    }

    private int parseAccountNumber(String accountNumber) {
        try {
            return Integer.parseInt(accountNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректный номер счета: " + accountNumber);
        }
    }

    private Optional<Account> findAccountByAccountNumber(int accountNumber) {
        return accountsRepository.findAccountByAccountNumber(accountNumber);
    }

    private TransactionDTO performTransfer(Account sender, Account receiver, BigDecimal amount) {
        BigDecimal convAmount = convertBalance(sender.getCurrency(), receiver.getCurrency(), amount);
        BigDecimal amountWithCom = convAmount.subtract(convAmount.multiply(BigDecimal.valueOf(FeeConfig.fee)));
        receiver.setBalance(receiver.getBalance().add(amountWithCom));
        sender.setBalance(sender.getBalance().subtract(amount));

        kafkaSenderService.sendMessage();

        accountsRepository.save(sender);
        accountsRepository.save(receiver);

        sendSocketService.sendToSocket(sender);
        sendSocketService.sendToSocket(receiver);

        outboxService.save(sender, amount);
        outboxService.save(receiver, amount);

        TransactionDTO transactionReceiver = new TransactionDTO();
        transactionReceiver.setTransactionId(String.valueOf(UUID.randomUUID()));
        transactionReceiver.setAmount(String.valueOf(receiver.getBalance()));
        transactionService.save(transactionReceiver, receiver.getAccountNumber(), amountWithCom.setScale(2, RoundingMode.HALF_EVEN));

        TransactionDTO transaction = new TransactionDTO();
        transaction.setAmount("-" + amount);
        return transaction;
    }

    private String takeToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = getMultiValueMapHttpEntity(headers);

        return Objects.requireNonNull(restTemplate.exchange(
                keycloakConfig.getKEYCLOAK_URL() + "/realms/" + keycloakConfig.getKEYCLOAK_REALM() + "/protocol/openid-connect/token",
                HttpMethod.POST,
                entity,
                RequestTokenAnswer.class
        ).getBody()).getAccessToken();
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(HttpHeaders headers) {
        RequestTokenDTO requestTokenDTO = new RequestTokenDTO(keycloakConfig.getClient_id(), keycloakConfig.getClient_secret());

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", requestTokenDTO.getGrant_type());
        requestBody.add("client_id", requestTokenDTO.getClient_id());
        requestBody.add("client_secret", requestTokenDTO.getClient_secret());

        return new HttpEntity<>(requestBody, headers);
    }

    private static boolean isPositiveNumber(String str) {
        return str.matches("[1-9][0-9]*");
    }
}
