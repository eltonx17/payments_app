package com.ezycollect.payment.service;

import com.ezycollect.payment.exception.RetriesExhaustedException;
import com.ezycollect.payment.model.Payment;
import com.ezycollect.payment.model.Webhook;
import com.ezycollect.payment.repository.WebhookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    WebhookNotificationServiceImpl webhookNotificationServiceImpl;

    @Autowired
    public WebhookServiceImpl(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    /**
     * Notify all registered webhooks asynchronously with retry mechanism
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
                futures.add(webhookNotificationServiceImpl.notifyWebhookAsync(webhook, payment));
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
        Webhook webhook = new Webhook();
        webhook.setUrl(url);
        try {
            webhookRepository.save(webhook);
        } catch (Exception e) {
            log.error("Failed to register webhook. DB may be down or nonresponsive - {}", e.getMessage());
            throw new RetriesExhaustedException("Database is down or nonresponsive during webhook registration", e);
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
