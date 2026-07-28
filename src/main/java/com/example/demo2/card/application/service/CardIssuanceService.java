package com.example.demo2.card.application.service;

import com.example.demo2.card.domain.model.PaymentCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardIssuanceService {

    public PaymentCard issueCard(String cardholderName) {

        String pan = generatePan();

        YearMonth expirationDate = YearMonth.now().plusYears(4);

        return PaymentCard.issue(
                pan,
                cardholderName,
                expirationDate
        );
    }

    /**
     * Генерирует PAN (Primary Account Number).
     *
     * Для учебного проекта используется случайная генерация.
     * В реальном банке PAN генерируется карточным процессингом
     * с использованием BIN/IIN и алгоритма Луна.
     */
    private String generatePan() {

        Random random = new Random();

        StringBuilder pan = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            pan.append(random.nextInt(10));
        }

        return pan.toString();
    }
}