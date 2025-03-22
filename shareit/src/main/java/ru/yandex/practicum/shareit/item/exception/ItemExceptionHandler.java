package ru.yandex.practicum.shareit.item.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.shareit.user.exception.DuplicateEmailException;

@RestControllerAdvice
public class ItemExceptionHandler {

    // Обработка попыток изменения вещи не владельцем
    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden
    public String handlePermissionDeniedException(PermissionDeniedException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404 Forbidden
    public String handleItemNotFoundException(ItemNotFoundException e) {
        return e.getMessage();
    }

}