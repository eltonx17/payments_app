package com.ezycollect.payment.service;

import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.dto.model.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@EnableAsync
public class WebhookNotificationServiceImpl implements WebhookNotificationService {

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;


    public WebhookNotificationServiceImpl(RestTemplate restTemplate, RetryTemplate retryTemplate) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }

    /**
     * Notify a single webhook asynchronously with retry mechanism
     * Failures are logged and retried based on the retry template configuration.
     * Upon exhausting retries, the failure is inserted into DB.
     * @param webhook
     * @param payment
     * @return CompletableFuture indicating success or failure
     */
    @Async
    @Override
    public CompletableFuture<Boolean> notifyWebhookAsync(Webhook webhook, Payment payment) {
        return CompletableFuture.completedFuture(retryTemplate.execute(context -> {
            //Setting context attributes for logging & audit purposes for handling failures
            context.setAttribute("url", webhook.getUrl());
            context.setAttribute("payload", payment);

            log.info("Notifying webhook at URL: {}", webhook.getUrl());
            ResponseEntity<String> response = restTemplate.postForEntity(webhook.getUrl(), payment, String.class);
            log.info("Webhook response status: {}", response.getStatusCode());
            log.info("Webhook response body: {}", response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        }));
    }
}
