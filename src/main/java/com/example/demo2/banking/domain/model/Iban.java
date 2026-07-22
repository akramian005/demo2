package com.example.demo2.banking.domain.model;

import java.util.Objects;

/**
 * Объект-значение (Value Object) для представления IBAN.
 * Гарантирует, что объект Iban не может существовать в невалидном состоянии.
 * Отвечает только за проверку формата — саму генерацию IBAN делает
 * IbanGenerationService в application-слое.
 */
public final class Iban {

    private final String value;

    private Iban(String value) {
        Objects.requireNonNull(value, "Номер IBAN не может быть пустым (null)");

        String cleanValue = value.replaceAll("\\s+", "").toUpperCase();

        if (cleanValue.length() < 15 || cleanValue.length() > 34) {
            throw new IllegalArgumentException("Неверная длина IBAN: " + cleanValue);
        }

        if (!cleanValue.substring(0, 2).matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("IBAN должен начинаться с кода страны: " + cleanValue);
        }

        this.value = cleanValue;
    }

    public static Iban of(String value) {
        return new Iban(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Iban iban = (Iban) o;
        return value.equals(iban.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}