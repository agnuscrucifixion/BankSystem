package com.example.randomnumber.services;

import com.example.randomnumber.config.ConverterConfig;
import com.example.randomnumber.config.ConverterConfigEnv;
import com.example.randomnumber.config.KeycloakConfig;
import com.example.randomnumber.dto.RequestTokenAnswer;
import com.example.randomnumber.exceptions.GetRatesException;
import com.example.randomnumber.generated.RatesResposne;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.util.Objects;

@Service
public class RatesService {
    private final ConverterConfigEnv converterConfigEnv;
    private final KeycloakConfig keycloakConfig;
    private final RestTemplate restTemplate;

    public RatesService(ConverterConfigEnv converterConfigEnv, KeycloakConfig keycloakConfig, RestTemplate restTemplate) {
        this.keycloakConfig = keycloakConfig;
        this.converterConfigEnv = converterConfigEnv;
        this.restTemplate = restTemplate;
    }

    @Retryable(retryFor = { GetRatesException.class, RestClientException.class, ConnectException.class }, maxAttempts = 4, backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 150))
    public RatesResposne getPermission() {
        String tokenForRates = takeToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenForRates);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<RatesResposne> resp = restTemplate.exchange(converterConfigEnv.getRatesUrl() + "/rates",
                HttpMethod.GET,
                entity,
                RatesResposne.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new GetRatesException("Не получилось взять курс");
        }

        return resp.getBody();
    }

    private String takeToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", keycloakConfig.getClient_id());
        requestBody.add("client_secret", keycloakConfig.getClient_secret());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<RequestTokenAnswer> responseEntity = restTemplate.exchange(
                keycloakConfig.getKEYCLOAK_URL() + "/realms/" + keycloakConfig.getKEYCLOAK_REALM() + "/protocol/openid-connect/token",
                HttpMethod.POST,
                entity,
                RequestTokenAnswer.class
        );
        return Objects.requireNonNull(responseEntity.getBody()).getAccessToken();
    }
}
