package ru.yandex.practicum.shareit.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.shareit.exception.ErrorResponse;

@RestControllerAdvice
public class BookingExceptionHandler {

    @ExceptionHandler(BookingDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingDateException(BookingDateException e) {
        return new ErrorResponse(e);
    }

    @ExceptionHandler(InvalidStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidStatusException(InvalidStatusException e) {
        return new ErrorResponse(e);
    }
}