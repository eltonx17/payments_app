package com.ezycollect.payment.dto.mapper;

import com.ezycollect.payment.dto.CreatePaymentRequest;
import com.ezycollect.payment.dto.CreatePaymentResponse;
import com.ezycollect.payment.dto.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct Mapper for converting between Payment entity and DTOs.
 */

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "cardNumber", ignore = true)
    Payment toModel(CreatePaymentRequest createPaymentRequest);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "message", source = "message")
    CreatePaymentResponse toDto(Payment payment, String status, String message);
}
