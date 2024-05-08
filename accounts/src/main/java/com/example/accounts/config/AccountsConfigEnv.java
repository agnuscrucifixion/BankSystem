package com.example.accounts.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
@Getter
public class AccountsConfigEnv {
    HashSet<String> currencies = new HashSet<>(Set.of("RUB", "USD", "GBP", "EUR", "CYN"));
    @Value("${CONVERTER_URL}")
    String CONVERTER_URL;

    @Value("${NOTIFICATION_SERVICE_URL}")
    String NOTIFICATION_SERVICE_URL;
    public String getNotificationServiceUrl() {
        return NOTIFICATION_SERVICE_URL;
    }
}
