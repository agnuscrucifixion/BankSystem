package com.example.accounts.exceptions;

public class GetRateLimitException extends RuntimeException{

    public GetRateLimitException(String messanger) {
        super(messanger);
    }
}
