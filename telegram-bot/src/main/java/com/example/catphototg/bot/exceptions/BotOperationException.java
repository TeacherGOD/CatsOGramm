package com.example.catphototg.bot.exceptions;

public class BotOperationException extends RuntimeException {
    public BotOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}