package com.ezycollect.payment.service;

import com.ezycollect.payment.model.Payment;
import org.springframework.http.ResponseEntity;

public interface WebhookService {

    ResponseEntity<Object> notifyWebhooks(Payment payment);

    void registerWebhook(String url);
}
