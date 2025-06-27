package com.example.catphototg.catservice.exceptions;

public class ReactionException extends RuntimeException {
    public ReactionException(String message, Throwable e) {
        super(message,e);
    }
}
