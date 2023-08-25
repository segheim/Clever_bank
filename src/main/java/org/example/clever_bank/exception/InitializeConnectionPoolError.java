package org.example.clever_bank.exception;

public class InitializeConnectionPoolError extends Error {
    public InitializeConnectionPoolError(String message, Throwable cause) {
        super(message, cause);
    }
}
