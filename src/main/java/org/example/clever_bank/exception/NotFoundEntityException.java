package org.example.clever_bank.exception;

/**
 * Exception if entity is not found
 */
public class NotFoundEntityException extends RuntimeException {

    public NotFoundEntityException(String message) {
        super(message);
    }
}
