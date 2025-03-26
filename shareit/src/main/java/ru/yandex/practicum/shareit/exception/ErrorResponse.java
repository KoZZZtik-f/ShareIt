package ru.yandex.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse {
    @JsonProperty("Error")
    private String error;

    @JsonProperty("Error message")
    private String description;

    public ErrorResponse(Throwable e) {
        error = e.getClass().getSimpleName();
        description = e.getMessage();
    }
}
