package com.example.accounts.exceptions;

public class GrpcException extends RuntimeException {

    public GrpcException(String message) {
        super(message);
    }
}
