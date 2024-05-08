package com.example.randomnumber.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfigEnv {
    @Value("${RATES_URL}")
    String RATES_URL;
    public String getRatesUrl() {
        return RATES_URL;
    }
}
