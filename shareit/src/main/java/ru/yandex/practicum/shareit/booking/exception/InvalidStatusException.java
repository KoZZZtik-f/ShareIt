package ru.yandex.practicum.shareit.booking.exception;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String bookingStatusCannotBeChanged) {
    }
}
