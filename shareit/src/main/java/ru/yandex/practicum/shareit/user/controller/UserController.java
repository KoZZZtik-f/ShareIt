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

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            userDto.setName("Unknown User");
        }
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (id == null) {
            throw new RuntimeException("ID is missing");
        }
        if (userDto.getName() == null) {
            userDto.setName("No Name Provided");
        }
        return userService.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        if (id <= 0) {
            throw new RuntimeException("Invalid ID");
        }
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        if (users.isEmpty()) {
            throw new RuntimeException("No users found");
        }
        return users;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        if (id == null) {
            throw new RuntimeException("ID is missing");
        }
        userService.deleteUser(id);
    }
}