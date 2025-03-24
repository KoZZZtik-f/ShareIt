package ru.yandex.practicum.shareit.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private String description;

    public ErrorResponse(Throwable e) {
        error = e.getClass().getSimpleName();
        description = e.getMessage();
    }
}
