package com.ezycollect.payment.service;

import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.dto.model.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class WebhookNotificationServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    @InjectMocks
    private WebhookNotificationServiceImpl webhookNotificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void notifyWebhookAsync_success() throws ExecutionException, InterruptedException {
        Webhook webhook = new Webhook();
        webhook.setUrl("http://test.com");
        Payment payment = new Payment();

        when(retryTemplate.execute(any())).thenAnswer(invocation -> {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
            when(restTemplate.postForEntity(eq(webhook.getUrl()), any(Payment.class), eq(String.class)))
                    .thenReturn(responseEntity);
            return invocation.getArgument(0, org.springframework.retry.RetryCallback.class).doWithRetry(Mockito.mock(RetryContext.class));
        });

        CompletableFuture<Boolean> future = webhookNotificationService.notifyWebhookAsync(webhook, payment);

        assertTrue(future.get());
    }

    @Test
    void notifyWebhookAsync_failure_non2xx() throws ExecutionException, InterruptedException {
        Webhook webhook = new Webhook();
        webhook.setUrl("http://test.com");
        Payment payment = new Payment();

        when(retryTemplate.execute(any())).thenAnswer(invocation -> {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
            when(restTemplate.postForEntity(eq(webhook.getUrl()), any(Payment.class), eq(String.class)))
                    .thenReturn(responseEntity);
            return invocation.getArgument(0, org.springframework.retry.RetryCallback.class).doWithRetry(Mockito.mock(RetryContext.class));
        });

        CompletableFuture<Boolean> future = webhookNotificationService.notifyWebhookAsync(webhook, payment);

        assertFalse(future.get());
    }

    @Test
    void notifyWebhookAsync_failure_exception() {
        Webhook webhook = new Webhook();
        webhook.setUrl("http://test.com");
        Payment payment = new Payment();

        when(retryTemplate.execute(any())).thenAnswer(invocation -> {
            when(restTemplate.postForEntity(eq(webhook.getUrl()), any(Payment.class), eq(String.class)))
                    .thenThrow(new RestClientException("Connection failed"));
            return invocation.getArgument(0, org.springframework.retry.RetryCallback.class).doWithRetry(Mockito.mock(RetryContext.class));
        });

        CompletableFuture<Boolean> future = webhookNotificationService.notifyWebhookAsync(webhook, payment);

        assertThrows(ExecutionException.class, future::get);
    }
}