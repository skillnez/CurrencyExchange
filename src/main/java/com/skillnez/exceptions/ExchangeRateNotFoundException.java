package com.skillnez.exceptions;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public ExchangeRateNotFoundException() {
        super("Exchange rate not found");
    }

    public ExchangeRateNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
