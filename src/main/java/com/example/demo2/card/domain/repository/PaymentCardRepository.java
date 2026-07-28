package com.example.demo2.card.domain.repository;

import com.example.demo2.card.domain.model.PaymentCard;

import java.util.Optional;

public interface PaymentCardRepository {

    PaymentCard save(PaymentCard card);

    Optional<PaymentCard> findByPan(String pan);
}
