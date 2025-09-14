package com.ezycollect.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

@Data
public class CreatePaymentRequest {

    @NotBlank
    private String requestId;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String zipCode;
    @NotBlank
    @CreditCardNumber
    private String cardNumber;
}
