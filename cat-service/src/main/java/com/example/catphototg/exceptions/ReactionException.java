package com.example.catphototg.exceptions;

public class ReactionException extends RuntimeException {
    public ReactionException(String message, Throwable e) {
        super(message,e);
    }
}
