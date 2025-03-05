package com.skillnez.exceptions;

public class DaoException extends RuntimeException {
    public DaoException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
