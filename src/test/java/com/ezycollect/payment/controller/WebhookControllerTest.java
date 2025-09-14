package com.ezycollect.payment.controller;

import com.ezycollect.payment.dto.RegisterWebhookRequest;
import com.ezycollect.payment.dto.RegisterWebhookResponse;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.ezycollect.payment.config.PaymentConstants.REGISTER_WEBHOOK_FAILED;
import static com.ezycollect.payment.config.PaymentConstants.REGISTER_WEBHOOK_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.anyString;

class WebhookControllerTest {

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private WebhookController webhookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerWebhook_success() throws DatabaseException {
        // Given
        RegisterWebhookRequest request = new RegisterWebhookRequest();
        request.setUrl("http://test.com/webhook");
        doNothing().when(webhookService).registerWebhook(anyString());

        // When
        ResponseEntity<RegisterWebhookResponse> response = webhookController.registerWebhook(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(REGISTER_WEBHOOK_SUCCESS, response.getBody().getStatus());
    }

    @Test
    void registerWebhook_failure() throws DatabaseException {
        // Given
        RegisterWebhookRequest request = new RegisterWebhookRequest();
        request.setUrl("http://test.com/webhook");
        doThrow(new DatabaseException("DB error", new Throwable(""))).when(webhookService).registerWebhook(anyString());

        // When
        ResponseEntity<RegisterWebhookResponse> response = webhookController.registerWebhook(request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(REGISTER_WEBHOOK_FAILED, response.getBody().getStatus());
    }
}
