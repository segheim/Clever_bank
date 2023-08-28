package org.example.clever_bank.exception;

public class ServiceException extends RuntimeException{

    public ServiceException(String message) {
        super(message);
    }
}
