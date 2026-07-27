package com.example.demo2.card.application.service;

import com.example.demo2.card.domain.model.PaymentCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardIssuanceService {

    public PaymentCard issueCard(String cardholderName) {
        String fullCardNumber = generateCardNumber();
        return PaymentCard.issue(fullCardNumber, cardholderName, LocalDate.now().plusYears(4));
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}