package com.ezycollect.payment.service;

import com.ezycollect.payment.dto.CreatePaymentRequest;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.exception.RetriesExhaustedException;
import com.ezycollect.payment.dto.mapper.PaymentMapper;
import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.repository.PaymentRepository;
import com.ezycollect.payment.util.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final WebhookService webhookService;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(WebhookService webhookService, PaymentMapper paymentMapper, PaymentRepository paymentRepository) {
        this.webhookService = webhookService;
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Create a new payment, save to DB, and notify webhooks asynchronously
     * @param createPaymentRequest
     * @return Saved Payment object
     */
    @Override
    public Payment createPayment(CreatePaymentRequest createPaymentRequest) {
        Payment payment = paymentMapper.toModel(createPaymentRequest);
        payment.setCardNumber(EncryptionUtil.encrypt(createPaymentRequest.getCardNumber()));
        Payment savedPayment;
        try {
            savedPayment = paymentRepository.save(payment);
            log.info("DB Insert successful");
        } catch (Exception e) {
            log.error("Failed to save payment to DB - {}", e.getMessage());
            throw new DatabaseException("Database is down or nonresponsive", e);
        }
        CompletableFuture.runAsync(() -> {
            try {
                webhookService.notifyWebhooks(savedPayment);
            } catch (Exception e) {
                log.error("Failed to notify webhook", e);
                throw new RetriesExhaustedException("Webhook notification failed", e);
            }
        });
        return savedPayment;
    }
}
