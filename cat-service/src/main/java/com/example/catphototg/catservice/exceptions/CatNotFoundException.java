package com.example.catphototg.catservice.exceptions;

public class CatNotFoundException extends RuntimeException {
    public CatNotFoundException(String message) {
        super(message);
    }
}