package ru.yandex.practicum.shareit.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.yandex.practicum.shareit.booking.exception.InvalidStatusException;
import ru.yandex.practicum.shareit.booking.mapper.BookingMapper;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.BookingStatus;
import ru.yandex.practicum.shareit.booking.repository.BookingRepository;
import ru.yandex.practicum.shareit.exception.AvailabilityException;
import ru.yandex.practicum.shareit.exception.PermissionDeniedException;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.item.service.ItemService;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    // Создание бронирования
    public BookingDtoResponse createBooking(BookingDtoRequest dto, Long userId) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItem(dto.getItemId());

        if (!item.getAvailable()) {
            throw new AvailabilityException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Owner cannot book own item");
        }

        Booking booking = BookingMapper.toEntity(dto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    // Подтверждение или отклонение бронирования
    public BookingDtoResponse approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new PermissionDeniedException("Only owner can approve booking");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidStatusException("Booking status cannot be changed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(updatedBooking);
    }

    // Получение бронирования по ID
    public BookingDtoResponse getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id " + bookingId));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Access denied");
        }

        return BookingMapper.toDto(booking);
    }

    // Получение всех бронирований пользователя
    public List<BookingDtoResponse> getUserBookings(Long userId, String state, int from, int size) {
        userService.getUserById(userId); // Проверяем, что пользователь существует


        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByBooker(userId);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByBooker(userId);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByBooker(userId);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }

        return bookings.stream()
                .skip(from)
                .limit(size)
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    // Получение всех бронирований для вещей владельца
    public List<BookingDtoResponse> getOwnerBookings(Long ownerId, String state, int from, int size) {
        userService.getUserById(ownerId); // Проверяем, что владелец существует

        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByOwner(ownerId);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByOwner(ownerId);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByOwner(ownerId);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }

        return bookings.stream()
                .skip(from)
                .limit(size)
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
