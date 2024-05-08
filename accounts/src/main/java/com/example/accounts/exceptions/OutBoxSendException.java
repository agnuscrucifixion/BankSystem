package com.example.accounts.exceptions;

public class OutBoxSendException extends RuntimeException{
    public OutBoxSendException(String message) {
        super(message);
    }
}
