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
import ru.yandex.practicum.shareit.booking.model.State;
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
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, Long userId) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItem(bookingDtoRequest.getItemId());

        validateBookingCreation(item, userId);

        Booking booking = BookingMapper.toEntity(bookingDtoRequest, item, booker);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingDtoResponse approveBooking(Long bookingId, Long userId, boolean approved) {
        Booking booking = getBookingById(bookingId);

        validateBookingApproval(booking, userId);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(updatedBooking);
    }

    @Override
    public BookingDtoResponse getBooking(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        validateBookingAccess(booking, userId);

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDtoResponse> getUserBookings(Long userId, State state, int from, int size) {
        userService.getUserById(userId); // Проверяем, что пользователь существует

        List<Booking> bookings = getBookingsByState(userId, state, true);
        return paginateAndMapToDto(bookings, from, size);
    }

    @Override
    public List<BookingDtoResponse> getOwnerBookings(Long ownerId, State state, int from, int size) {
        userService.getUserById(ownerId); // Проверяем, что владелец существует

        List<Booking> bookings = getBookingsByState(ownerId, state, false);
        return paginateAndMapToDto(bookings, from, size);
    }

    // Вспомогательные методы

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id " + bookingId));
    }

    private void validateBookingCreation(Item item, Long userId) {
        if (!item.getAvailable()) {
            throw new AvailabilityException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Owner cannot book own item");
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
        switch (state) {
            case ALL:
                return isUserBookings
                        ? bookingRepository.findByBookerIdOrderByStartDesc(userId)
                        : bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT:
                return isUserBookings
                        ? bookingRepository.findCurrentByBooker(userId)
                        : bookingRepository.findCurrentByOwner(userId);
            case PAST:
                return isUserBookings
                        ? bookingRepository.findPastByBooker(userId)
                        : bookingRepository.findPastByOwner(userId);
            case FUTURE:
                return isUserBookings
                        ? bookingRepository.findFutureByBooker(userId)
                        : bookingRepository.findFutureByOwner(userId);
            case WAITING:
                return isUserBookings
                        ? bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                        : bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED:
                return isUserBookings
                        ? bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                        : bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    private List<BookingDtoResponse> paginateAndMapToDto(List<Booking> bookings, int from, int size) {
        return bookings.stream()
                .skip(from)
                .limit(size)
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}