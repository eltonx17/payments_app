package com.ezycollect.payment.dto;

import lombok.Data;

@Data
public class CreatePaymentRequest {

    private String requestId;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String cardNumber;
}
