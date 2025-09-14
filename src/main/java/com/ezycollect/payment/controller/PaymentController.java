package com.ezycollect.payment.controller;

import com.ezycollect.payment.dto.CreatePaymentRequest;
import com.ezycollect.payment.dto.CreatePaymentResponse;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.dto.mapper.PaymentMapper;
import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.exception.PaymentFailedException;
import com.ezycollect.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ezycollect.payment.config.PaymentConstants.PAYMENT_CREATED;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    /**
     * Endpoint to create a new payment
     * Validation is performed on the request body using @Valid - invalid requests will result in 400 Bad Request
     * For more granular validation error details, a custom exception handler can be implemented
     * @param createPaymentRequest The payment creation request payload
     * @return ResponseEntity containing the payment creation response and HTTP status
     */
    @PostMapping("/create")
    public ResponseEntity<CreatePaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest createPaymentRequest) {
        try {
            log.info("Received new payment creation request - RequestID: {}", createPaymentRequest.getRequestId());
            Payment createdPayment = paymentService.createPayment(createPaymentRequest);
            log.info("Payment created with ID: {}", createdPayment.getRequestId());
            return new ResponseEntity<>(paymentMapper.toDto(createdPayment, PAYMENT_CREATED, null), HttpStatus.CREATED);
        } catch (DatabaseException e) {
            log.error("Payment Creation Failed for RequestID: {}", createPaymentRequest.getRequestId());
            throw new PaymentFailedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid arguments for payment creation for RequestID: {}", createPaymentRequest.getRequestId());
            throw new PaymentFailedException(e.getMessage());
        }
    }
}
