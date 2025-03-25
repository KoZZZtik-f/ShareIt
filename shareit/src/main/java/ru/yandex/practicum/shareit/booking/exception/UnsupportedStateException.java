package ru.yandex.practicum.shareit.booking.exception;

import ru.yandex.practicum.shareit.booking.model.State;

public class UnsupportedStateException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unknown state: %s";

    public UnsupportedStateException(State state) {
        super(String.format(DEFAULT_MESSAGE, state.toString()));
    }

    public UnsupportedStateException(String stateStr) {
        super(String.format(DEFAULT_MESSAGE, stateStr));
    }
}
