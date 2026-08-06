package com.example.demo2.payment.repository;

import com.example.demo2.payment.model.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    Optional<PaymentCard> findByPan(String pan);

}