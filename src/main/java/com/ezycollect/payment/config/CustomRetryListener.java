package com.ezycollect.payment.config;

import com.ezycollect.payment.exception.RetriesExhaustedException;
import com.ezycollect.payment.dto.model.WebhookAudit;
import com.ezycollect.payment.repository.WebhookAuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomRetryListener extends RetryListenerSupport {

    private final WebhookAuditRepository webhookAuditRepository;
    private final ObjectMapper objectMapper;

    /**
     * On each retry attempt, log the error and retry count
     */
    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.error("Error invoking URL {}: {}", context.getAttribute("url"), throwable.getMessage());
        log.warn("Retrying operation for the {} time", context.getRetryCount());
        super.onError(context, callback, throwable);
    }

    /**
     * On exhausting retries, log the failure and save to WebhookAudit table
     */
    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (throwable != null) {
            log.info("Retries exhausted after {} attempts for URL {}", context.getRetryCount(), context.getAttribute("url"));
            Object payload = context.getAttribute("payload");
            String url = (String) context.getAttribute("url");

            if (payload != null && url != null) {
                try {
                    String payloadAsString = objectMapper.writeValueAsString(payload);

                    WebhookAudit webhookAudit = new WebhookAudit();
                    webhookAudit.setPayload(payloadAsString);
                    webhookAudit.setUrl(url);
                    webhookAudit.setTimestamp(LocalDateTime.now());
                    webhookAudit.setError(throwable.getMessage());

                    webhookAuditRepository.save(webhookAudit);
                    log.info("Saved failed webhook audit to database.");
                } catch (JsonProcessingException e) {
                    log.error("Could not serialize payload to JSON for audit.", e);
                }
            }

            throw new RetriesExhaustedException("Retries exhausted for operation. Payload: " + (payload != null ? payload.toString() : "N/A"), throwable);
        }
        super.close(context, callback, throwable);
    }
}
