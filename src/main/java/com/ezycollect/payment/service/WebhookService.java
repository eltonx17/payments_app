package com.ezycollect.payment.service;

import com.ezycollect.payment.dto.model.Payment;

public interface WebhookService {

    void notifyWebhooks(Payment payment);

    void registerWebhook(String url);
}
