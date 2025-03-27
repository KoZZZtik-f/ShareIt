package ru.yandex.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.BookingStatus;
import ru.yandex.practicum.shareit.booking.repository.BookingRepository;
import ru.yandex.practicum.shareit.exception.PermissionDeniedException;
import ru.yandex.practicum.shareit.item.model.Comment;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.item.repository.CommentRepository;
import ru.yandex.practicum.shareit.item.repository.ItemRepository;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//TODO: переделать на hamcrest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=share_it_test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ItemServiceImplTest {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemService itemService;

    @Test
    void addItem_ShouldCreateItemWithOwner() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, null);

        // Act
        Item savedItem = itemService.addItem(item, owner.getId());

        // Assert
        assertNotNull(savedItem.getId());
        assertEquals("Дрель", savedItem.getName());
        assertEquals(owner.getId(), savedItem.getOwner().getId());
    }

    @Test
    void editItem_ShouldUpdateFields_WhenUserIsOwner() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        Item original = itemService.addItem(
                createTestItem("Дрель", "Старое описание", true, owner),
                owner.getId()
        );

        Item updates = new Item();
        updates.setName("Перфоратор");
        updates.setDescription("Новое описание");
        updates.setAvailable(false);

        // Act
        Item updatedItem = itemService.editItem(original.getId(), updates, owner.getId());

        // Assert
        assertEquals("Перфоратор", updatedItem.getName());
        assertEquals("Новое описание", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void editItem_ShouldThrow_WhenUserNotOwner() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User notOwner = createTestUser("notowner@email.com");
        Item item = itemService.addItem(
                createTestItem("Дрель", "Описание", true, owner),
                owner.getId()
        );

        // Act & Assert
        assertThrows(PermissionDeniedException.class, () ->
                itemService.editItem(item.getId(), new Item(), notOwner.getId())
        );
    }

    @Test
    void getItem_ShouldReturnItem_WhenExists() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        Item expected = itemService.addItem(
                createTestItem("Дрель", "Описание", true, owner),
                owner.getId()
        );

        // Act
        Item actual = itemService.getItem(expected.getId());

        // Assert
        assertEquals(expected.getId(), actual.getId());
        assertEquals("Дрель", actual.getName());
    }

    @Test
    void getItemsByOwner_ShouldReturnOwnersItems() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        Item item1 = itemService.addItem(createTestItem("Дрель", "1", true, owner), owner.getId());
        Item item2 = itemService.addItem(createTestItem("Молоток", "2", true, owner), owner.getId());

        // Act
        List<Item> items = itemService.getItemsByOwner(owner.getId());

        // Assert
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Дрель")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Молоток")));
    }

    @Test
    void searchItems_ShouldReturnAvailableItemsContainingText() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        itemService.addItem(createTestItem("Дрель", "Аккумуляторная", true, owner), owner.getId());
        itemService.addItem(createTestItem("Молоток", "Стандартный", true, owner), owner.getId());
        itemService.addItem(createTestItem("Пила", "Аккумуляторная", false, owner), owner.getId());

        // Act
        List<Item> foundItems = itemService.searchItems("аккумуляторная");

        // Assert
        assertEquals(1, foundItems.size());
        assertEquals("Дрель", foundItems.get(0).getName());
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsBlank() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        itemService.addItem(createTestItem("Дрель", "Описание", true, owner), owner.getId());

        // Act
        List<Item> result = itemService.searchItems("");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_ShouldCreateComment_WhenUserHadBookings() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User commentAuthor = createTestUser("author@email.com");
        Item item = itemService.addItem(
                createTestItem("Дрель", "Аккумуляторная", true, owner),
                owner.getId()
        );

        // Создаем завершенное бронирование (имитируем через репозиторий)
        createCompletedBooking(item.getId(), commentAuthor, LocalDateTime.now().minusDays(2));

        String commentText = "Хорошая дрель!";

        // Act
        Comment createdComment = itemService.addComment(item.getId(), commentText, commentAuthor.getId());

        // Assert
        assertNotNull(createdComment.getId());
        assertEquals(commentText, createdComment.getText());
        assertEquals(commentAuthor.getId(), createdComment.getAuthor().getId());
        assertEquals(item.getId(), createdComment.getItem().getId());
        assertNotNull(createdComment.getCreated());
    }

    @Test
    void addComment_ShouldThrow_WhenUserHadNoBookings() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User notBooker = createTestUser("notbooker@email.com");
        Item item = itemService.addItem(
                createTestItem("Дрель", "Аккумуляторная", true, owner),
                owner.getId()
        );

        String commentText = "Хорошая дрель!";

        // Act & Assert
        assertThrows(PermissionDeniedException.class, () ->
                itemService.addComment(item.getId(), commentText, notBooker.getId())
        );
    }

    @Test
    void getItem_ShouldReturnItemWithComments() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User commentAuthor = createTestUser("author@email.com");
        Item item = itemService.addItem(
                createTestItem("Дрель", "Аккумуляторная", true, owner),
                owner.getId()
        );

        createCompletedBooking(item.getId(), commentAuthor, LocalDateTime.now().minusDays(1));
        itemService.addComment(item.getId(), "Отличная вещь!", commentAuthor.getId());

        // Act
        Item foundItem = itemService.getItem(item.getId());

        // Assert
        assertEquals(1, foundItem.getComments().size());
        assertEquals("Отличная вещь!", foundItem.getComments().get(0).getText());
    }







    // Вспомогательный метод для создания завершенного бронирования
    private void createCompletedBooking(long itemId, User booker, LocalDateTime endTime) {

        bookingRepository.save(Booking.builder()
                .id(itemId)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(endTime.minusDays(1))
                .end(endTime)
                .build());
    }

    private User createTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("Test User");
        return userService.createUser(user);
    }

    private Item createTestItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return item;
    }
}