package ru.yandex.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.exception.BookingDateException;
import ru.yandex.practicum.shareit.booking.exception.InvalidStatusException;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.BookingStatus;
import ru.yandex.practicum.shareit.booking.model.State;
import ru.yandex.practicum.shareit.booking.repository.BookingRepository;
import ru.yandex.practicum.shareit.exception.AvailabilityException;
import ru.yandex.practicum.shareit.exception.PermissionDeniedException;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.item.service.ItemService;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=share_it_test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class BookingServiceImplTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Test
    void createBooking_ShouldCreateBooking_WhenValidData() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);

        BookingDtoRequest bookingDto = new BookingDtoRequest(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        // Act
        Booking createdBooking = bookingService.createBooking(bookingDto, booker.getId());

        // Assert
        assertNotNull(createdBooking.getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertEquals(item.getId(), createdBooking.getItem().getId());
        assertEquals(booker.getId(), createdBooking.getBooker().getId());
    }

    @Test
    void createBooking_ShouldThrow_WhenItemNotAvailable() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", false, owner);

        BookingDtoRequest bookingDto = new BookingDtoRequest(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        // Act & Assert
        assertThrows(AvailabilityException.class, () ->
                bookingService.createBooking(bookingDto, booker.getId())
        );
    }

    @Test
    void createBooking_ShouldThrow_WhenOwnerBooksOwnItem() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);

        BookingDtoRequest bookingDto = new BookingDtoRequest(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        // Act & Assert
        assertThrows(PermissionDeniedException.class, () ->
                bookingService.createBooking(bookingDto, owner.getId())
        );
    }

    @Test
    void createBooking_ShouldThrow_WhenEndDateBeforeStartDate() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);

        BookingDtoRequest bookingDto = new BookingDtoRequest(
                item.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        // Act & Assert
        assertThrows(BookingDateException.class, () ->
                bookingService.createBooking(bookingDto, booker.getId())
        );
    }

    @Test
    void approveBooking_ShouldApprove_WhenOwnerApproves() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking booking = createTestBooking(item, booker, BookingStatus.WAITING);

        // Act
        Booking approvedBooking = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        // Assert
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void approveBooking_ShouldReject_WhenOwnerRejects() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking booking = createTestBooking(item, booker, BookingStatus.WAITING);

        // Act
        Booking rejectedBooking = bookingService.approveBooking(booking.getId(), owner.getId(), false);

        // Assert
        assertEquals(BookingStatus.REJECTED, rejectedBooking.getStatus());
    }

    @Test
    void approveBooking_ShouldThrow_WhenNotOwnerApproves() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        User otherUser = createTestUser("other@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking booking = createTestBooking(item, booker, BookingStatus.WAITING);

        // Act & Assert
        assertThrows(PermissionDeniedException.class, () ->
                bookingService.approveBooking(booking.getId(), otherUser.getId(), true)
        );
    }

    @Test
    void approveBooking_ShouldThrow_WhenStatusNotWaiting() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking booking = createTestBooking(item, booker, BookingStatus.APPROVED);

        // Act & Assert
        assertThrows(InvalidStatusException.class, () ->
                bookingService.approveBooking(booking.getId(), owner.getId(), true)
        );
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenOwnerRequests() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking expected = createTestBooking(item, booker, BookingStatus.APPROVED);

        // Act
        Booking actual = bookingService.getBooking(expected.getId(), owner.getId());

        // Assert
        assertEquals(expected.getId(), actual.getId());
        assertEquals(item.getId(), actual.getItem().getId());
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenBookerRequests() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking expected = createTestBooking(item, booker, BookingStatus.APPROVED);

        // Act
        Booking actual = bookingService.getBooking(expected.getId(), booker.getId());

        // Assert
        assertEquals(expected.getId(), actual.getId());
        assertEquals(booker.getId(), actual.getBooker().getId());
    }

    @Test
    void getBooking_ShouldThrow_WhenUnauthorizedUserRequests() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        User otherUser = createTestUser("other@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking booking = createTestBooking(item, booker, BookingStatus.APPROVED);

        // Act & Assert
        assertThrows(PermissionDeniedException.class, () ->
                bookingService.getBooking(booking.getId(), otherUser.getId())
        );
    }

    @Test
    void getUserBookings_ShouldReturnUserBookings() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking booking1 = createTestBooking(item, booker, BookingStatus.APPROVED);
        Booking booking2 = createTestBooking(item, booker, BookingStatus.REJECTED);

        // Act
        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), State.ALL, 0, 10);

        // Assert
        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().anyMatch(b -> b.getId().equals(booking1.getId())));
        assertTrue(bookings.stream().anyMatch(b -> b.getId().equals(booking2.getId())));
    }

    @Test
    void getOwnerBookings_ShouldReturnOwnerBookings() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        Booking booking1 = createTestBooking(item, booker, BookingStatus.APPROVED);
        Booking booking2 = createTestBooking(item, booker, BookingStatus.WAITING);

        // Act
        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), State.ALL, 0, 10);

        // Assert
        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().anyMatch(b -> b.getId().equals(booking1.getId())));
        assertTrue(bookings.stream().anyMatch(b -> b.getId().equals(booking2.getId())));
    }

    @Test
    void hasUserBookedItem_ShouldReturnTrue_WhenUserBookedItem() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        createTestBooking(item, booker, BookingStatus.APPROVED, LocalDateTime.now().minusDays(2));

        // Act
        boolean hasBooked = bookingService.hasUserBookedItem(item.getId(), booker.getId());

        // Assert
        assertTrue(hasBooked);
    }

    @Test
    void hasUserBookedItem_ShouldReturnFalse_WhenUserNotBookedItem() {
        // Arrange
        User owner = createTestUser("owner@email.com");
        User booker = createTestUser("booker@email.com");
        User otherUser = createTestUser("other@email.com");
        Item item = createTestItem("Дрель", "Аккумуляторная дрель", true, owner);
        createTestBooking(item, booker, BookingStatus.APPROVED, LocalDateTime.now().minusDays(2));

        // Act
        boolean hasBooked = bookingService.hasUserBookedItem(item.getId(), otherUser.getId());

        // Assert
        assertFalse(hasBooked);
    }

    // Вспомогательные методы

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
        return itemService.addItem(item, owner.getId());
    }

    private Booking createTestBooking(Item item, User booker, BookingStatus status) {
        return createTestBooking(item, booker, status, LocalDateTime.now().plusDays(1));
    }

    private Booking createTestBooking(Item item, User booker, BookingStatus status, LocalDateTime start) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(start.plusDays(1));
        return bookingRepository.save(booking);
    }
}