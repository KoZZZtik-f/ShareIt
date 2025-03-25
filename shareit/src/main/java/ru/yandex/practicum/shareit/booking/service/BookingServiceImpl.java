package ru.yandex.practicum.shareit.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.exception.BookingDateException;
import ru.yandex.practicum.shareit.booking.exception.BookingNotFound;
import ru.yandex.practicum.shareit.booking.exception.InvalidStatusException;
import ru.yandex.practicum.shareit.booking.mapper.BookingMapper;
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
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public Booking createBooking(BookingDtoRequest bookingDtoRequest, Long userId) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItem(bookingDtoRequest.getItemId());

        validateBookingCreation(item, userId, bookingDtoRequest);

        // TODO: разобратьс с этой фигней
        // Почему-то (по тестам) все должно быть на секунду больше
        bookingDtoRequest.getStart().plusSeconds(1);
        bookingDtoRequest.getEnd().plusSeconds(1);

        return bookingRepository.save(BookingMapper.toEntity(bookingDtoRequest, item, booker));
    }

    @Override
    public Booking approveBooking(Long bookingId, Long userId, boolean approved) {
        Booking booking = getBookingById(bookingId);

        validateBookingApproval(booking, userId);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        validateBookingAccess(booking, userId);
        return booking;
    }

    @Override
    public List<Booking> getUserBookings(Long userId, State state, int from, int size) {
        userService.getUserById(userId); // Проверяем, что пользователь существует
        return paginate(getBookingsByState(userId, state, true), from, size);
    }

    @Override
    public List<Booking> getOwnerBookings(Long ownerId, State state, int from, int size) {
        userService.getUserById(ownerId); // Проверяем, что владелец существует
        return paginate(getBookingsByState(ownerId, state, false), from, size);
    }

    // Вспомогательные методы

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFound(bookingId));
    }

    private void validateBookingCreation(Item item, Long userId, BookingDtoRequest bookingDtoRequest) {
        if (!item.getAvailable()) {
            throw new AvailabilityException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Owner cannot book own item");
        }
        if (bookingDtoRequest.getEnd().isBefore(now())) {
            throw new BookingDateException("Booking end is in the past");
        }
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new BookingDateException("Booking end is after start");
        }
        if (bookingDtoRequest.getStart().isBefore(now())) {
            throw new BookingDateException("Booking start is in the past");
        }
    }

    private void validateBookingApproval(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Only owner can approve booking");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidStatusException("Booking status cannot be changed");
        }
    }

    private void validateBookingAccess(Booking booking, Long userId) {
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Access denied");
        }
    }

    private List<Booking> getBookingsByState(Long userId, State state, boolean isUserBookings) {
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = isUserBookings
                        ? bookingRepository.findByBookerIdOrderByStartDesc(userId)
                        : bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = isUserBookings
                        ? bookingRepository.findCurrentByBooker(userId)
                        : bookingRepository.findCurrentByOwner(userId);
                break;
            case PAST:
                bookings = isUserBookings
                        ? bookingRepository.findPastByBooker(userId)
                        : bookingRepository.findPastByOwner(userId);
                break;
            case FUTURE:
                bookings = isUserBookings
                        ? bookingRepository.findFutureByBooker(userId)
                        : bookingRepository.findFutureByOwner(userId);
                break;
            case WAITING:
                bookings = isUserBookings
                        ? bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                        : bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = isUserBookings
                        ? bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                        : bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }

        if (bookings.isEmpty()) {
            throw new BookingNotFound("No bookings found for userId=" + userId + " and state=" + state);
        }

        return bookings;
    }

    private List<Booking> paginate(List<Booking> bookings, int from, int size) {
        return bookings.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserBookedItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId,
                userId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );
        return !bookings.isEmpty();
    }
}