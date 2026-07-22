package com.example.demo2.shared.event;

/**
 * Событие о регистрации нового пользователя.
 * Публикуется модулем identity, слушают shop и banking —
 * без прямой зависимости identity от этих модулей.
 */
public record UserRegisteredEvent(Long userId, String email) {
}