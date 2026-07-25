package com.example.demo2.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Address address;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Builder.Default
    @Column(name = "bonus_points")
    private Integer bonusPoints = 0;

    public void addBonusPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Начисляемые баллы не могут быть отрицательными");
        }
        this.bonusPoints += points;
    }

    public void spendBonusPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Списываемые баллы не могут быть отрицательными");
        }
        if (this.bonusPoints < points) {
            throw new IllegalStateException("Недостаточно бонусных баллов");
        }
        this.bonusPoints -= points;
    }
}