package com.ezycollect.payment.exception;

public class WebhookRegistrationFailedException extends RuntimeException {
    public WebhookRegistrationFailedException(String message) {
        super(message);
    }
}
