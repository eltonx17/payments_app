package com.ezycollect.payment.service;

import com.ezycollect.payment.dto.CreatePaymentRequest;
import com.ezycollect.payment.model.Payment;

public interface PaymentService {

    Payment createPayment(CreatePaymentRequest createPaymentRequest);
}
