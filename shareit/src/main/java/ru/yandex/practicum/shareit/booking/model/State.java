package ru.yandex.practicum.shareit.booking.model;

public enum State {
    ALL,       // Все бронирования
    CURRENT,   // Текущие бронирования
    PAST,      // Завершенные бронирования
    FUTURE,    // Будущие бронирования
    WAITING,   // Бронирования, ожидающие подтверждения
    REJECTED   // Отклоненные бронирования
}