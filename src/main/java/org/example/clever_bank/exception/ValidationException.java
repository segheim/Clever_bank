package org.example.clever_bank.exception;

/**
 * Exception if data is not valid
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
