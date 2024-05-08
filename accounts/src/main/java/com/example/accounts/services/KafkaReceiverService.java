package com.example.accounts.services;

import com.example.accounts.dto.MessageKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaReceiverService {

    private final AdminService adminService;

    @Autowired
    public KafkaReceiverService(AdminService adminService) {
        this.adminService = adminService;
    }

    @KafkaListener(topics = "${kafka.fee}")
    void listener(MessageKafka message) {
        if (message.getAction().equals("UPDATE_FEE")) {
            adminService.readFromDB();
        }
    }
}
