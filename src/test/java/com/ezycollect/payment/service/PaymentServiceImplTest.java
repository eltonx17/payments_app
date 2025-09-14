package com.ezycollect.payment.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ezycollect.payment.dto.CreatePaymentRequest;
import com.ezycollect.payment.dto.mapper.PaymentMapper;
import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.repository.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Mock
    private WebhookService webhookService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Logger paymentServiceLogger = (Logger) LoggerFactory.getLogger(PaymentServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        paymentServiceLogger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        Logger paymentServiceLogger = (Logger) LoggerFactory.getLogger(PaymentServiceImpl.class);
        listAppender.stop();
        paymentServiceLogger.detachAppender(listAppender);
    }

    @Test
    void createPayment() {
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRequestId(UUID.randomUUID().toString());
        createPaymentRequest.setFirstName("John");
        createPaymentRequest.setLastName("Doe");
        createPaymentRequest.setZipCode("12345");
        createPaymentRequest.setCardNumber("1234-5678-9012-3456");

        Payment payment = new Payment();
        payment.setRequestId(createPaymentRequest.getRequestId());
        payment.setFirstName(createPaymentRequest.getFirstName());
        payment.setLastName(createPaymentRequest.getLastName());
        payment.setZipCode(createPaymentRequest.getZipCode());
        payment.setCardNumber("encrypted-card-number");

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setRequestId(payment.getRequestId());
        savedPayment.setFirstName(payment.getFirstName());
        savedPayment.setLastName(payment.getLastName());
        savedPayment.setZipCode(payment.getZipCode());
        savedPayment.setCardNumber(payment.getCardNumber());

        when(paymentMapper.toModel(any(CreatePaymentRequest.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Payment result = paymentService.createPayment(createPaymentRequest);

        assertEquals(savedPayment, result);
        verify(webhookService, timeout(1000)).notifyWebhooks(savedPayment);
    }

    @Test
    void createPayment_databaseException() {
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRequestId(UUID.randomUUID().toString());
        createPaymentRequest.setFirstName("John");
        createPaymentRequest.setLastName("Doe");
        createPaymentRequest.setZipCode("12345");
        createPaymentRequest.setCardNumber("1234-5678-9012-3456");

        Payment payment = new Payment();
        payment.setRequestId(createPaymentRequest.getRequestId());
        payment.setFirstName(createPaymentRequest.getFirstName());
        payment.setLastName(createPaymentRequest.getLastName());
        payment.setZipCode(createPaymentRequest.getZipCode());
        payment.setCardNumber("encrypted-card-number");

        when(paymentMapper.toModel(any(CreatePaymentRequest.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("DB is down"));

        assertThrows(DatabaseException.class, () -> {
            paymentService.createPayment(createPaymentRequest);
        });
    }

    @Test
    void createPayment_webhookNotificationFails() throws InterruptedException {
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRequestId(UUID.randomUUID().toString());
        createPaymentRequest.setFirstName("John");
        createPaymentRequest.setLastName("Doe");
        createPaymentRequest.setZipCode("12345");
        createPaymentRequest.setCardNumber("1234-5678-9012-3456");

        Payment payment = new Payment();
        payment.setRequestId(createPaymentRequest.getRequestId());
        payment.setFirstName(createPaymentRequest.getFirstName());
        payment.setLastName(createPaymentRequest.getLastName());
        payment.setZipCode(createPaymentRequest.getZipCode());
        payment.setCardNumber("encrypted-card-number");

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setRequestId(payment.getRequestId());
        savedPayment.setFirstName(payment.getFirstName());
        savedPayment.setLastName(payment.getLastName());
        savedPayment.setZipCode(payment.getZipCode());
        savedPayment.setCardNumber(payment.getCardNumber());

        CountDownLatch latch = new CountDownLatch(1);

        when(paymentMapper.toModel(any(CreatePaymentRequest.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doAnswer(invocation -> {
            latch.countDown();
            throw new RuntimeException("Webhook is down");
        }).when(webhookService).notifyWebhooks(any(Payment.class));

        paymentService.createPayment(createPaymentRequest);

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Webhook notification was not called");

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
    }
}