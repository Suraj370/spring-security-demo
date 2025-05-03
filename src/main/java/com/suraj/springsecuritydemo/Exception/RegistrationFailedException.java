package com.suraj.springsecuritydemo.Exception;

public class RegistrationFailedException extends RuntimeException {
    public RegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationFailedException(String message) {
        super(message);
    }
}
