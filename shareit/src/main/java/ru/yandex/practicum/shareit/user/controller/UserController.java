package ru.yandex.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareit.user.dto.UserDto;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Создание нового пользователя
    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    // Обновление пользователя
    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    // Получение пользователя по ID
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Получение списка всех пользователей
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // Удаление пользователя по ID
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
