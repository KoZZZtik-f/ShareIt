package ru.yandex.practicum.shareit.user.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User with id %d not found";

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(long userId) {
        super(String.format(DEFAULT_MESSAGE, userId));
    }
}