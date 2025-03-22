package ru.yandex.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.user.dto.UserDto;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден."));

        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        try {
            return userRepository.save(existingUser);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
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
        userRepository.deleteById(id);
    }
}