package com.ezycollect.payment.service;

import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.dto.model.Webhook;

import java.util.concurrent.CompletableFuture;

public interface WebhookNotificationService {
    CompletableFuture<Boolean> notifyWebhookAsync(Webhook webhook, Payment payment);
}
