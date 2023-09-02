package org.example.clever_bank.exception;

/**
 * Exception when connection pool is not initialised
 */
public class InitializeConnectionPoolError extends Error {
    public InitializeConnectionPoolError(String message, Throwable cause) {
        super(message, cause);
    }
}
