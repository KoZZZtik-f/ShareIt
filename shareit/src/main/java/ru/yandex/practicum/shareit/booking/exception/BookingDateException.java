package ru.yandex.practicum.shareit.booking.exception;

public class BookingDateException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Проблема с датами бронирования";

    public BookingDateException(String message) {
        super(message);
    }

    public BookingDateException() {
        super(DEFAULT_MESSAGE);
    }
}
