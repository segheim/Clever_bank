package org.example.clever_bank.exception;

/**
 * Exception for authentication
 */
public class AuthenticateException extends Exception{

    public AuthenticateException(String message, Throwable cause) {
        super(message, cause);
    }
}
