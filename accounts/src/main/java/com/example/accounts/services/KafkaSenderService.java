package com.example.accounts.services;

import com.example.accounts.dto.MessageKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaSenderService {

    @Value("${kafka.fee}")
    String topicName;

    private final KafkaTemplate<String, MessageKafka> kafkaTemplate;
    @Autowired
    public KafkaSenderService(KafkaTemplate<String, MessageKafka> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage() {
        kafkaTemplate.send(topicName, new MessageKafka());
    }
}
