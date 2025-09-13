package com.ezycollect.payment.exception;

public class RetriesExhaustedException extends RuntimeException {
    public RetriesExhaustedException(String message) {
        super(message);
    }

    public RetriesExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }
}

