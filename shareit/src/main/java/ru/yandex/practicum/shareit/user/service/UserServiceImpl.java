package ru.yandex.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.user.exception.DuplicateEmailException;
import ru.yandex.practicum.shareit.user.exception.UserNotFoundException;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        // Проверяем, существует ли пользователь с таким email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userUpdates) {
        // 1. Находим существующего пользователя
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден."));

        // 2. Проверяем и обновляем имя (если предоставлено)
        if (userUpdates.getName() != null && !userUpdates.getName().isBlank()) {
            existingUser.setName(userUpdates.getName());
        }

        // 3. Проверяем и обновляем email (с дополнительными проверками)
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().isBlank()) {
            // Если email не изменился - пропускаем проверку
            if (!userUpdates.getEmail().equals(existingUser.getEmail())) {
                // Проверяем, не занят ли email другим пользователем
                if (userRepository.existsByEmailAndIdNot(userUpdates.getEmail(), id)) {
                    throw new DuplicateEmailException("Пользователь с email " + userUpdates.getEmail() + " уже существует.");
                }
                existingUser.setEmail(userUpdates.getEmail());
            }
        }

        // 4. Сохраняем обновленные данные
        return userRepository.save(existingUser);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден."));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
    }
}