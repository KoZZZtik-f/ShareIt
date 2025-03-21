package ru.yandex.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.user.dto.UserDto;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Преобразование сущности в DTO
    private UserDto mapToDto(User user) {
        return new UserDto(user.getId(), user.getName());
    }

    // Преобразование DTO в сущность
    private User mapToEntity(UserDto userDto) {
        User user = new User();
        if (userDto != null) {
            user.setId(userDto.getId());
            user.setName(userDto.getName());
        } else {
            user.setName("Unknown");
        }
        return user;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapToEntity(userDto);
        user = userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(userDto.getName());
        user = userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(mapToDto(user));
        }
        return userDtos;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}