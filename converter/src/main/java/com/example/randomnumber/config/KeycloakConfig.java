package com.example.randomnumber.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KeycloakConfig {

    @Value("${KEYCLOAK_URL}")
    String KEYCLOAK_URL;
    @Value("${KEYCLOAK_REALM}")
    String KEYCLOAK_REALM;
    @Value("${CLIENT_ID}")
    String client_id;
    @Value("${CLIENT_SECRET}")
    String client_secret;
}
