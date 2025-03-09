package com.skillnez.exceptions;

public class IncorrectRequestException extends RuntimeException {
    public IncorrectRequestException(String errorMessage) {
        super(errorMessage);
    }
}
