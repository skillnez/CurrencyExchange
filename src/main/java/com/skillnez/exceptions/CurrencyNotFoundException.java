package com.skillnez.exceptions;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public CurrencyNotFoundException() {
        super("Currency not found");
    }

    public CurrencyNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
