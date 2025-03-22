package ru.yandex.practicum.shareit.user.service;

import ru.yandex.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User updateUser(Long id, User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    void deleteUser(Long id);
}
