package com.ezycollect.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterWebhookResponse {
    private String status;
    private String error;

    public RegisterWebhookResponse(String status) {
        this.status = status;
    }
}
