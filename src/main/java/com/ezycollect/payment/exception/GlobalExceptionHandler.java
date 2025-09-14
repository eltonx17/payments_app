package com.ezycollect.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ezycollect.payment.config.PaymentConstants.PAYMENT_FAILED;
import static com.ezycollect.payment.config.PaymentConstants.REGISTER_WEBHOOK_FAILED;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors for method arguments
     * @param ex The MethodArgumentNotValidException exception
     * @return ResponseEntity containing the error details and HTTP status
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", "VALIDATION_ERROR");
        errorResponse.put("error", errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle PaymentFailedException
     * @param ex The PaymentFailedException exception
     * @return ResponseEntity containing the error details and HTTP status
     */
    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<Object> handlePaymentFailedException(PaymentFailedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", PAYMENT_FAILED);
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle WebhookRegistrationFailedException
     * @param ex The WebhookRegistrationFailedException exception
     * @return ResponseEntity containing the error details and HTTP status
     */
    @ExceptionHandler(WebhookRegistrationFailedException.class)
    public ResponseEntity<Object> handleWebhookRegistrationFailedException(WebhookRegistrationFailedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", REGISTER_WEBHOOK_FAILED);
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
