package com.ezycollect.payment.controller;

import com.ezycollect.payment.dto.RegisterWebhookRequest;
import com.ezycollect.payment.dto.RegisterWebhookResponse;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.exception.WebhookRegistrationFailedException;
import com.ezycollect.payment.service.WebhookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ezycollect.payment.config.PaymentConstants.REGISTER_WEBHOOK_SUCCESS;

@RestController
@RequestMapping("/v1/webhooks")
@AllArgsConstructor
@CrossOrigin
@Slf4j
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    /**
     * Endpoint to register a new webhook
     * @param registerWebhookRequest The webhook registration request payload
     * @return ResponseEntity containing the webhook registration response and HTTP status
     */
    @PostMapping("/create")
    public ResponseEntity<RegisterWebhookResponse> registerWebhook(@RequestBody RegisterWebhookRequest registerWebhookRequest) {
        try {
            webhookService.registerWebhook(registerWebhookRequest.getUrl());
            return ResponseEntity.ok(new RegisterWebhookResponse(REGISTER_WEBHOOK_SUCCESS));
        } catch (DatabaseException e) {
            throw new WebhookRegistrationFailedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new WebhookRegistrationFailedException(e.getMessage());
        }
    }
}
