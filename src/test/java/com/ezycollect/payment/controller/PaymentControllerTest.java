package com.ezycollect.payment.controller;

import com.ezycollect.payment.dto.CreatePaymentRequest;
import com.ezycollect.payment.dto.CreatePaymentResponse;
import com.ezycollect.payment.dto.mapper.PaymentMapper;
import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.exception.PaymentFailedException;
import com.ezycollect.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.ezycollect.payment.config.PaymentConstants.PAYMENT_CREATED;
import static com.ezycollect.payment.config.PaymentConstants.PAYMENT_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPayment_success() throws DatabaseException {
        // Given
        CreatePaymentRequest request = new CreatePaymentRequest();
        Payment payment = new Payment();
        CreatePaymentResponse responseDto = new CreatePaymentResponse();
        responseDto.setStatus(PAYMENT_CREATED);

        when(paymentService.createPayment(any(CreatePaymentRequest.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment, PAYMENT_CREATED, "message")).thenReturn(responseDto);

        // When
        ResponseEntity<CreatePaymentResponse> response = paymentController.createPayment(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createPayment_failure() {
        // Given
        CreatePaymentRequest request = new CreatePaymentRequest();
        CreatePaymentResponse responseDto = new CreatePaymentResponse();
        responseDto.setStatus(PAYMENT_FAILED);

        when(paymentService.createPayment(any(CreatePaymentRequest.class))).thenThrow(new DatabaseException("DB error", new Throwable("")));
        when(paymentMapper.toDto(any(Payment.class), any(String.class), any(String.class))).thenReturn(responseDto);

        // When, Then
        assertThrows(PaymentFailedException.class, () -> paymentController.createPayment(request));
    }
}