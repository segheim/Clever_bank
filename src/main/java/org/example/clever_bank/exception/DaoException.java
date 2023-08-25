package org.example.clever_bank.exception;

public class DaoException extends RuntimeException {

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
