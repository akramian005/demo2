package com.example.demo2.payment.repository;

import com.example.demo2.payment.model.entity.PaymentCard;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentCardRepository {

    PaymentCard save(PaymentCard card);

    Optional<PaymentCard> findByPan(String pan);
}
