package ru.yandex.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AvailabilityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAvailabilityException(AvailabilityException e) {
        return new ErrorResponse(e);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handlePermissionDeniedException(PermissionDeniedException e) {
        return new ErrorResponse(e);
    }

}