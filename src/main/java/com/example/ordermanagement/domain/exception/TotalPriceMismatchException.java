package com.example.ordermanagement.domain.exception;

public class TotalPriceMismatchException extends RuntimeException {
    public TotalPriceMismatchException(String message) {
        super(message);
    }
}

