package ru.yandex.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareit.user.dto.UserDto;
import ru.yandex.practicum.shareit.user.mapper.UserMapper;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Создание нового пользователя
    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User createdUser = userService.createUser(user);
        return UserMapper.toDto(createdUser);
    }

    // Обновление пользователя
    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User updatedUser = userService.updateUser(id, user);
        return UserMapper.toDto(updatedUser);
    }

    // Получение пользователя по ID
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.toDto(user);
    }

    // Получение списка всех пользователей
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    // Удаление пользователя по ID
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
