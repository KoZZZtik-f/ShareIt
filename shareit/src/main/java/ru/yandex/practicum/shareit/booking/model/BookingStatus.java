package ru.yandex.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING,  // Ожидает подтверждения
    APPROVED, // Подтверждено владельцем
    REJECTED, // Отклонено владельцем
    CANCELED  // Отменено пользователем
}
