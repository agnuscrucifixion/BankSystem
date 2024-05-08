package com.example.randomnumber.services;

import com.example.randomnumber.config.ConverterConfigEnv;
import com.example.randomnumber.config.KeycloakConfig;
import com.example.randomnumber.dto.ConvertResponse;
import com.example.randomnumber.dto.RequestTokenAnswer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.randomnumber.generated.RatesResposne;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import converter.ConverterServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import com.example.randomnumber.generated.Currency;

@GrpcService
public class ConverterServiceGrpcServer extends ConverterServiceGrpc.ConverterServiceImplBase {

    private final RestTemplate restTemplate;
    private final RatesService ratesService;

    @Autowired
    public ConverterServiceGrpcServer(RestTemplate restTemplate, RatesService ratesService) {
        this.restTemplate = restTemplate;
        this.restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        this.ratesService = ratesService;
    }

    @Override
    public void convert(converter.ConvertRequest request, StreamObserver<converter.ConvertResponse> responseObserver) {
        Currency first = Currency.fromValue(request.getFrom());
        Currency second = Currency.fromValue(request.getTo());

        BigDecimal amount = BigDecimal.valueOf(request.getAmount());


        ConvertResponse convertResponse = performConversion(first, second, amount, ratesService.getPermission());
        converter.ConvertResponse response = converter.ConvertResponse
                .newBuilder()
                .setCurrency(convertResponse.getCurrency())
                .setAmount(convertResponse.getAmount().doubleValue()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /*public ResponseEntity<ConvertResponse> convert(@RequestParam String from, @RequestParam String to, @RequestParam BigDecimal amount) {
        Currency first = Currency.fromValue(from);
        Currency second = Currency.fromValue(to);

        if (first == null || second == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }

        String tokenForRates = takeToken();

        if (tokenForRates == null) {
            return ResponseEntity.internalServerError().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenForRates);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        String jsonResponse;
        try {
            jsonResponse = restTemplate.exchange("http://rates:8080/rates",
                    HttpMethod.GET,
                    entity,
                    String.class).getBody();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        generated.RatesResposne ratesResposne;
        try {
            ratesResposne = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        ConvertResponse convertResponse = performConversion(first, second, amount, ratesResposne);
        if (convertResponse == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(convertResponse);
    }*/



    private ConvertResponse performConversion(Currency first, Currency second, BigDecimal amount, RatesResposne ratesResposne) {
        ConvertResponse convertResponse = new ConvertResponse();
        try {
            if (!first.getValue().equals("RUB") && !second.getValue().equals("RUB")) {
                BigDecimal firstCount = ratesResposne.getRates().get(first.getValue()).multiply(amount);
                BigDecimal result = firstCount.divide(ratesResposne.getRates().get(second.getValue()), 2, RoundingMode.HALF_EVEN);
                convertResponse.setCurrency(second.getValue());
                convertResponse.setAmount(result.setScale(2, RoundingMode.HALF_EVEN));
            } else if (first.getValue().equals("RUB") && second.getValue().equals("RUB")) {
                convertResponse.setCurrency(second.getValue());
                convertResponse.setAmount(amount.setScale(2, RoundingMode.HALF_EVEN));
            } else {
                if (first.getValue().equals("RUB")) {
                    convertResponse.setCurrency(second.getValue());
                    convertResponse.setAmount(amount.divide(ratesResposne.getRates().get(second.getValue()), 2, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN));
                } else {
                    convertResponse.setCurrency(second.getValue());
                    convertResponse.setAmount(ratesResposne.getRates().get(first.getValue()).multiply(amount).setScale(2, RoundingMode.HALF_EVEN));
                }
            }
            return convertResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
