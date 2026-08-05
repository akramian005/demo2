package com.example.demo2.payment.model.enums.converter;

import com.example.demo2.payment.model.enums.PaymentTransactionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentTransactionStatusConverter implements AttributeConverter<PaymentTransactionStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaymentTransactionStatus status) {
        return status == null ? null : status.name();
    }

    @Override
    public PaymentTransactionStatus convertToEntityAttribute(String status) {
        return status == null ? null : PaymentTransactionStatus.fromString(status);
    }
}
