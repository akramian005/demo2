package com.example.demo2.card.infrastructure.persistence.jpa;

import com.example.demo2.card.domain.model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataPaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    Optional<PaymentCard> findByPan(String pan);
}
