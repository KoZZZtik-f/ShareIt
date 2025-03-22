package ru.yandex.practicum.shareit.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    // Обработка дублирования email
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
    public String handleDublicateEmailException(DuplicateEmailException ex) {
        return ex.getMessage();
    }

    // Обработка отсутствия пользователя
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404 Not Found
    public String handleUserNotFoundException(UserNotFoundException ex) {
        return ex.getMessage();
    }

    // Обработка ошибок валидации
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
    public String handleConstraintViolationException(jakarta.validation.ConstraintViolationException ex) {
        return "Ошибка валидации: " + ex.getMessage();
    }

    // Обработка других исключений, связанных с пользователями
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
    public String handleDataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException ex) {
        return "Ошибка целостности данных: " + ex.getMessage();
    }

}
