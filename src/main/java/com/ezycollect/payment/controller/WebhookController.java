package com.ezycollect.payment.controller;

import com.ezycollect.payment.dto.RegisterWebhookRequest;
import com.ezycollect.payment.dto.RegisterWebhookResponse;
import com.ezycollect.payment.exception.DatabaseException;
import com.ezycollect.payment.service.WebhookService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ezycollect.payment.config.PaymentConstants.REGISTER_WEBHOOK_FAILED;
import static com.ezycollect.payment.config.PaymentConstants.REGISTER_WEBHOOK_SUCCESS;

@RestController
@RequestMapping("/v1/webhooks")
@AllArgsConstructor
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
            return new ResponseEntity<>(new RegisterWebhookResponse(REGISTER_WEBHOOK_FAILED), HttpStatus.BAD_REQUEST);
        }
    }
}
