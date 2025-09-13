package com.ezycollect.payment.mapper;

import com.ezycollect.payment.dto.CreatePaymentRequest;
import com.ezycollect.payment.dto.CreatePaymentResponse;
import com.ezycollect.payment.model.Payment;
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
    CreatePaymentResponse toDto(Payment payment, String status);
}
