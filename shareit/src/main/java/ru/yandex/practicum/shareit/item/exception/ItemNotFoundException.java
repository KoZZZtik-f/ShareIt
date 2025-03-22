package ru.yandex.practicum.shareit.item.exception;

public class ItemNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Item not found";

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}

