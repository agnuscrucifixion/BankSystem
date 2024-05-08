package com.example.accounts.services;

import com.example.accounts.dto.AccountSocketDTO;
import com.example.accounts.entities.Account;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;

@Service
public class SendSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public SendSocketService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    public void sendToSocket(Account account) {
        AccountSocketDTO accountSocketDTO = new AccountSocketDTO();
        accountSocketDTO.setAccountNumber(account.getAccountNumber());
        accountSocketDTO.setCurrency(account.getCurrency());
        accountSocketDTO.setBalance(account.getBalance().setScale(2, RoundingMode.HALF_EVEN));
        simpMessagingTemplate.convertAndSend("/topic/accounts", accountSocketDTO);
    }

}
