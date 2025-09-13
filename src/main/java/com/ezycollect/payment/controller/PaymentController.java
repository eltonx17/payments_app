package com.ezycollect.payment.controller;

import com.ezycollect.payment.dto.CreatePaymentRequest;
import com.ezycollect.payment.dto.CreatePaymentResponse;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.mapper.PaymentMapper;
import com.ezycollect.payment.model.Payment;
import com.ezycollect.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ezycollect.payment.constants.PaymentConstants.PAYMENT_CREATED;
import static com.ezycollect.payment.constants.PaymentConstants.PAYMENT_FAILED;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    /**
     * Endpoint to create a new payment
     * @param createPaymentRequest The payment creation request payload
     * @return ResponseEntity containing the payment creation response and HTTP status
     */
    @PostMapping("/create")
    public ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        try {
            log.info("Received new payment creation request - RequestID: {}", createPaymentRequest.getRequestId());
            Payment createdPayment = paymentService.createPayment(createPaymentRequest);
            log.info("Payment created with ID: {}", createdPayment.getRequestId());
            return new ResponseEntity<>(paymentMapper.toDto(createdPayment, PAYMENT_CREATED), HttpStatus.CREATED);
        } catch (DatabaseException e) {
            log.error("Payment Creation Failed for RequestID: {}", createPaymentRequest.getRequestId());
            return new ResponseEntity<>(paymentMapper.toDto(new Payment(), PAYMENT_FAILED), HttpStatus.BAD_REQUEST);
        }
    }
}
