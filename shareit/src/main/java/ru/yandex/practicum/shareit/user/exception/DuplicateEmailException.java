package ru.yandex.practicum.shareit.user.exception;

public class DuplicateEmailException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Такой email уже существует";

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException() {
        super(DEFAULT_MESSAGE);
    }

}