package com.ezycollect.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterWebhookResponse {
    private String status;
    private String message;

    public RegisterWebhookResponse(String status) {
        this.status = status;
    }

    public RegisterWebhookResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}

