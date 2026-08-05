package com.example.demo2.payment.model.enums;

public enum RefundStatus {
    COMPLETED,
    FAILED;

    public static RefundStatus fromString(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("RefundStatus не может быть пустым");
        }
        for (RefundStatus refundStatus : RefundStatus.values()) {
            if (refundStatus.name().equalsIgnoreCase(status.trim())) {
                return refundStatus;
            }
        }
        throw new IllegalArgumentException("Invalid RefundStatus " + status);
    }
}
