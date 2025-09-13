package com.ezycollect.payment.service;

import com.ezycollect.payment.dto.model.Payment;
import com.ezycollect.payment.dto.model.Webhook;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.repository.WebhookRepository;
import com.ezycollect.payment.util.UrlValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebhookServiceImplTest {

    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private WebhookNotificationService webhookNotificationService;

    @Spy
    UrlValidationUtils urlValidationUtils;

    @InjectMocks
    private WebhookServiceImpl webhookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void notifyWebhooks_noWebhooks() {
        when(webhookRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = webhookService.notifyWebhooks(new Payment());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("No webhooks registered", ((Map<?, ?>) response.getBody()).get("status"));
    }

    @Test
    void notifyWebhooks_allSuccess() {
        Webhook webhook = new Webhook();
        webhook.setUrl("http://test.com");
        List<Webhook> webhooks = Collections.singletonList(webhook);

        when(webhookRepository.findAll()).thenReturn(webhooks);
        when(webhookNotificationService.notifyWebhookAsync(any(Webhook.class), any(Payment.class)))
                .thenReturn(CompletableFuture.completedFuture(true));

        ResponseEntity<Object> response = webhookService.notifyWebhooks(new Payment());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("OK", ((Map<?, ?>) response.getBody()).get("status"));
    }

    @Test
    void notifyWebhooks_partialFailure() {
        Webhook webhook1 = new Webhook();
        webhook1.setUrl("http://success.com");
        Webhook webhook2 = new Webhook();
        webhook2.setUrl("http://fail.com");
        List<Webhook> webhooks = Arrays.asList(webhook1, webhook2);

        when(webhookRepository.findAll()).thenReturn(webhooks);
        when(webhookNotificationService.notifyWebhookAsync(eq(webhook1), any(Payment.class)))
                .thenReturn(CompletableFuture.completedFuture(true));
        when(webhookNotificationService.notifyWebhookAsync(eq(webhook2), any(Payment.class)))
                .thenReturn(CompletableFuture.completedFuture(false));

        ResponseEntity<Object> response = webhookService.notifyWebhooks(new Payment());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("Partial Failure", ((Map<?, ?>) response.getBody()).get("status"));
    }

    @Test
    void registerWebhook_success() {
        String url = "http://mywebhook.com";
        webhookService.registerWebhook(url);

        ArgumentCaptor<Webhook> webhookCaptor = ArgumentCaptor.forClass(Webhook.class);
        verify(webhookRepository).save(webhookCaptor.capture());

        assertEquals(url, webhookCaptor.getValue().getUrl());
    }

    @Test
    void registerWebhook_databaseException() {
        String url = "http://mywebhook.com";
        doThrow(new RuntimeException("DB error")).when(webhookRepository).save(any(Webhook.class));

        assertThrows(DatabaseException.class, () -> {
            webhookService.registerWebhook(url);
        });
    }
}