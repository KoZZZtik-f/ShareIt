package ru.yandex.practicum.shareit.user.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.shareit.user.exception.DuplicateEmailException;
import ru.yandex.practicum.shareit.user.exception.UserNotFoundException;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=share_it_test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class UserServiceImplTest {

    private final UserRepository userRepository;
    private final UserService userService;

    @Test
    public void createUser_ShouldCreateNewUser() {
        // Given
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");

        // When
        User createdUser = userService.createUser(newUser);

        // Then
        assertNotNull(createdUser.getId());
        assertEquals("New User", createdUser.getName());
        assertEquals("new@example.com", createdUser.getEmail());
    }

    @Test
    public void createUser_WithExistingEmail_ShouldThrowException() {
        // Given
        User existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail("existing@example.com");
        userRepository.save(existingUser);

        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("existing@example.com");

        // When & Then
        assertThrows(DuplicateEmailException.class, () -> userService.createUser(newUser));
    }

    @Test
    public void updateUser_ShouldUpdateUserFields() {
        // Given
        User existingUser = new User();
        existingUser.setName("Original Name");
        existingUser.setEmail("original@example.com");
        User savedUser = userRepository.save(existingUser);

        User updateData = new User();
        updateData.setName("Updated Name");
        updateData.setEmail("updated@example.com");

        // When
        User updatedUser = userService.updateUser(savedUser.getId(), updateData);

        // Then
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    public void updateUser_PartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Given
        User existingUser = new User();
        existingUser.setName("Original Name");
        existingUser.setEmail("original@example.com");
        User savedUser = userRepository.save(existingUser);

        User updateData = new User();
        updateData.setEmail("updated@example.com");

        // When
        User updatedUser = userService.updateUser(savedUser.getId(), updateData);

        // Then
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Original Name", updatedUser.getName()); // Имя не изменилось
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    public void updateUser_WithNonExistingId_ShouldThrowException() {
        // Given
        User updateData = new User();
        updateData.setName("Test Name");

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(999L, updateData));
    }

    @Test
    public void updateUser_WithDuplicateEmail_ShouldThrowException() {
        // Given
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        User savedUser2 = userRepository.save(user2);

        User updatedUser = new User();
        updatedUser.setEmail("user1@example.com");

        // When & Then
        assertThrows(DuplicateEmailException.class,
                // email: "user2@example.com" -> "user1@example.com"
                () -> userService.updateUser(savedUser2.getId(), updatedUser));
    }

    @Test
    public void updateUser_WithSameEmail_ShouldNotThrowException() {
        // Given
        User user = new User();
        user.setName("Original Name");
        user.setEmail("user@example.com");
        User savedUser = userRepository.save(user);

        User updateData = new User();
        updateData.setEmail("user@example.com"); // Тот же email
        updateData.setName("New Name");

        // When
        User updatedUser = userService.updateUser(savedUser.getId(), updateData);

        // Then - не должно быть исключения
        assertEquals("user@example.com", updatedUser.getEmail());
        assertEquals("New Name", updatedUser.getName());
    }

    @Test
    public void getUserById_ShouldReturnUser() {
        // Given
        User expectedUser = new User();
        expectedUser.setName("Test User");
        expectedUser.setEmail("test@example.com");
        User savedUser = userRepository.save(expectedUser);

        // When
        User actualUser = userService.getUserById(savedUser.getId());

        // Then
        assertEquals(savedUser.getId(), actualUser.getId());
        assertEquals("Test User", actualUser.getName());
        assertEquals("test@example.com", actualUser.getEmail());
    }

    @Test
    public void getUserById_WithNonExistingId_ShouldThrowException() {
        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    @Transactional
    public void deleteUser_ShouldDeleteUser() {
        // Given: создаём и сохраняем пользователя
        User user = new User();
        user.setName("To Delete");
        user.setEmail("delete@example.com");
        User savedUser = userRepository.save(user);

        // убеждаемся, что пользователь сохранён
        assertThat(userService.getAllUsers(), hasSize(1));
        assertNotNull(savedUser.getId());
        Optional<User> savedUserFromDb = userRepository.findById(savedUser.getId());
        assertTrue(savedUserFromDb.isPresent()
                && savedUserFromDb.get().equals(savedUser));

        // When: удаляем пользователя
        userService.deleteUser(savedUser.getId());

        // Then: проверяем, что пользователь действительно удалён
        // 1. Пытаемся найти пользователя по id
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent(), "User should be deleted");

        // 2. Проверяем, что список пользователей не содержит удалённого пользователя
        List<User> users = userService.getAllUsers();
        assertThat(users, hasSize(0));
        assertThat(users, not(hasItem(savedUser)));

        // Дополнительно можно проверить выброс исключения при попытке удаления несуществующего пользователя:
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(savedUser.getId()));
    }

    @Test
    public void deleteUser_WithNonExistingId_ShouldThrowException() {
        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
    }

    @Test
    public void getAllUsersTest() {
        // Подготовка тестовых данных: создаем двух пользователей
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);

        // Вызов тестируемого метода
        List<User> users = userService.getAllUsers();

        // Проверка, что список не null и содержит ровно 2 элемента
        assertNotNull(users);
        assertThat(users, hasSize(2));

        // Проверка, что список содержит пользователей с ожидаемыми именами и email
        assertThat(users, containsInAnyOrder(
                allOf(
                        hasProperty("name", is("User1")),
                        hasProperty("email", is("user1@example.com"))
                ),
                allOf(
                        hasProperty("name", is("User2")),
                        hasProperty("email", is("user2@example.com"))
                )
        ));
    }
}