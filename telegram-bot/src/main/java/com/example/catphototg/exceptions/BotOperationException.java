package com.example.catphototg.exceptions;

public class BotOperationException extends RuntimeException {
    public BotOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}