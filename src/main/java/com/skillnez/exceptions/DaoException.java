package com.skillnez.exceptions;

public class DaoException extends RuntimeException {
    public DaoException(Throwable e) {
        super(e);
    }
}
