package com.example.demo2.card.infrastructure.persistence;

import com.example.demo2.card.domain.model.PaymentCard;
import com.example.demo2.card.domain.repository.PaymentCardRepository;
import com.example.demo2.card.infrastructure.persistence.jpa.SpringDataPaymentCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentCardRepositoryImpl implements PaymentCardRepository {

    private final SpringDataPaymentCardRepository springDataRepository;

    @Override
    public PaymentCard save(PaymentCard card) {
        return springDataRepository.save(card);
    }

    @Override
    public Optional<PaymentCard> findByPan(String pan) {
        return springDataRepository.findByPan(pan);
    }
}
