package ru.yandex.practicum.shareit.booking.exception;

public class BookingNotFound extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Booking with id = %d not found";

    public BookingNotFound(String message) {
        super(message);
    }

    public BookingNotFound(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
