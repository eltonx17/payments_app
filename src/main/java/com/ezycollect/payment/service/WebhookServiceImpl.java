package com.ezycollect.payment.service;

import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.dto.model.Webhook;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.repository.WebhookRepository;
import com.ezycollect.payment.util.UrlValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class WebhookServiceImpl implements WebhookService {

    private final WebhookRepository webhookRepository;
    private final WebhookNotificationService webhookNotificationService;

    public WebhookServiceImpl(WebhookRepository webhookRepository, WebhookNotificationService webhookNotificationService) {
        this.webhookRepository = webhookRepository;
        this.webhookNotificationService = webhookNotificationService;
    }

    /**
     * Notify all registered webhooks asynchronously with retry mechanism
     * The payment object will contain encrypted card number - assuming the card number can be decrypted by the receiver
     * @param payment
     * @return ResponseEntity with overall status
     */
    @Override
    public ResponseEntity<Object> notifyWebhooks(Payment payment) {
        List<Webhook> webhooks = webhookRepository.findAll();
        if (!webhooks.isEmpty()) {
            log.info("Notifying {} registered webhooks", webhooks.size());
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();
            for (Webhook webhook : webhooks) {
                futures.add(webhookNotificationService.notifyWebhookAsync(webhook, payment));
            }
            boolean allSuccess = true;
            allSuccess = validateInvocations(futures, allSuccess);
            Map<String, String> response = new HashMap<>();
            response.put("status", allSuccess ? "OK" : "Partial Failure");
            return new ResponseEntity<>(response, allSuccess ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            log.info("No registered webhooks to invoke");
            Map<String, String> response = new HashMap<>();
            response.put("status", "No webhooks registered");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    /**
     * Register a new webhook URL
     * Validation of URL is performed via Aspect in aop package
     * before this method is invoked
     * @param url
     */
    @Override
    public void registerWebhook(String url) {
        if(url.isEmpty()) throw new IllegalArgumentException("Empty URL not allowed");

        String urlToValidate = url;
        // Prepend "https://" to URLs that start with "www." but don't have a scheme.
        if (url.toLowerCase().startsWith("www.") && !url.contains("://")) {
            urlToValidate = "https://" + url;
        }
        if(!UrlValidationUtils.validateUrl(urlToValidate)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        Webhook webhook = new Webhook();
        webhook.setUrl(urlToValidate);
        try {
            webhookRepository.save(webhook);
        } catch (Exception e) {
            log.error("Failed to register webhook. DB may be down or non responsive - {}", e.getMessage());
            throw new DatabaseException("Database is down or non responsive during webhook registration", e);
        }
    }

    /**
     * Helper method to validate the results of all
     * asynchronous webhook notifications
     * @param futures
     * @param allSuccess
     * @return boolean indicating if all notifications were successful
     */
    private static boolean validateInvocations(List<CompletableFuture<Boolean>> futures, boolean allSuccess) {
        for (CompletableFuture<Boolean> future : futures) {
            try {
                if (!future.get()) {
                    allSuccess = false;
                }
            } catch (InterruptedException | ExecutionException e) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }
}
