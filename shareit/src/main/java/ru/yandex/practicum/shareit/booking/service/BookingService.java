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

    public BookingDtoResponse createBooking(BookingDtoRequest dto, Long userId) {
        System.out.println("[DEBUG] createBooking called with dto=" + dto + " and userId=" + userId);

        User booker = userService.getUserById(userId);
        System.out.println("[DEBUG] Booker fetched: " + booker);
        Item item = itemService.getItem(dto.getItemId());
        System.out.println("[DEBUG] Item fetched: " + item);

        if (item.getAvailable() == null || !item.getAvailable()) {
            System.out.println("[ERROR] Item is not available");
            throw new AvailabilityException("Item is not available");
        }
        if (item.getOwner() == null) {
            System.out.println("[ERROR] Item owner is null");
            throw new AvailabilityException("Item has no owner");
        }
        if (item.getOwner().getId().equals(userId)) {
            System.out.println("[ERROR] Owner cannot book own item, userId=" + userId);
            throw new PermissionDeniedException("Owner cannot book own item");
        }

        System.out.println("[DEBUG] Mapping DTO to Booking entity");
        Booking booking = BookingMapper.toEntity(dto, item, booker);
        System.out.println("[DEBUG] Booking mapped: " + booking);

        Booking savedBooking = bookingRepository.save(booking);
        System.out.println("[DEBUG] Booking saved: " + savedBooking);
        savedBooking = bookingRepository.save(savedBooking);
        System.out.println("[DEBUG] Booking re-saved: " + savedBooking);

        return BookingMapper.toDto(savedBooking);
    }

    public BookingDtoResponse approveBooking(Long bookingId, Long ownerId, boolean approved) {
        System.out.println("[DEBUG] approveBooking called with bookingId=" + bookingId + ", ownerId=" + ownerId + ", approved=" + approved);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    System.out.println("[ERROR] Booking not found for id " + bookingId);
                    return new EntityNotFoundException("Booking not found with id " + bookingId);
                });

        if (booking.getItem() == null || booking.getItem().getOwner() == null || !booking.getItem().getOwner().getId().equals(ownerId)) {
            System.out.println("[ERROR] Only owner can approve booking. OwnerId: " + ownerId + ", booking owner: " + (booking.getItem() != null ? booking.getItem().getOwner() : "null"));
            throw new PermissionDeniedException("Only owner can approve booking");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            System.out.println("[ERROR] Booking status is not WAITING, current status: " + booking.getStatus());
            throw new InvalidStatusException("Booking status cannot be changed");
        }

        if (approved) {
            System.out.println("[DEBUG] Approving booking");
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            System.out.println("[DEBUG] Rejecting booking");
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        System.out.println("[DEBUG] Booking updated: " + updatedBooking);
        return BookingMapper.toDto(updatedBooking);
    }

    public BookingDtoResponse getBooking(Long bookingId, Long userId) {
        System.out.println("[DEBUG] getBooking called for bookingId=" + bookingId + " and userId=" + userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    System.out.println("[ERROR] Booking not found with id " + bookingId);
                    return new EntityNotFoundException("Booking not found with id " + bookingId);
                });

        if (booking.getBooker() == null || booking.getItem() == null ||
                (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId))) {
            System.out.println("[ERROR] Access denied for userId=" + userId);
            throw new PermissionDeniedException("Access denied");
        }
        System.out.println("[DEBUG] Booking access granted for userId=" + userId);
        return BookingMapper.toDto(booking);
    }

    public List<BookingDtoResponse> getUserBookings(Long userId, String state, int from, int size) {
        System.out.println("[DEBUG] getUserBookings called for userId=" + userId + " with state=" + state);
        userService.getUserById(userId); // Проверяем, что пользователь существует

        List<Booking> bookings;
        String stateUpper = state.toUpperCase();
        if (stateUpper.equals("ALL")) {
            bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        } else if (stateUpper.equals("CURRENT")) {
            bookings = bookingRepository.findCurrentByBooker(userId);
        } else if (stateUpper.equals("PAST")) {
            bookings = bookingRepository.findPastByBooker(userId);
        } else if (stateUpper.equals("FUTURE")) {
            bookings = bookingRepository.findFutureByBooker(userId);
        } else if (stateUpper.equals("WAITING")) {
            bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
        } else if (stateUpper.equals("REJECTED")) {
            bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        } else {
            System.out.println("[ERROR] Unknown state: " + state);
            throw new IllegalArgumentException("Unknown state: " + state);
        }

        System.out.println("[DEBUG] Total bookings found: " + bookings.size());
        List<BookingDtoResponse> responseList = bookings.stream()
                .skip(from)
                .limit(size)
                .map(booking -> {
                    System.out.println("[DEBUG] Mapping booking: " + booking);
                    return BookingMapper.toDto(booking);
                })
                .collect(Collectors.toList());
        System.out.println("[DEBUG] Returning " + responseList.size() + " bookings");
        return responseList;
    }

    public List<BookingDtoResponse> getOwnerBookings(Long ownerId, String state, int from, int size) {
        System.out.println("[DEBUG] getOwnerBookings called for ownerId=" + ownerId + " with state=" + state);
        userService.getUserById(ownerId); // Проверяем, что владелец существует

        List<Booking> bookings;
        String stateUpper = state.toUpperCase();
        if (stateUpper.equals("ALL")) {
            bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        } else if (stateUpper.equals("CURRENT")) {
            bookings = bookingRepository.findCurrentByOwner(ownerId);
        } else if (stateUpper.equals("PAST")) {
            bookings = bookingRepository.findPastByOwner(ownerId);
        } else if (stateUpper.equals("FUTURE")) {
            bookings = bookingRepository.findFutureByOwner(ownerId);
        } else if (stateUpper.equals("WAITING")) {
            bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
        } else if (stateUpper.equals("REJECTED")) {
            bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        } else {
            System.out.println("[ERROR] Unknown state: " + state);
            throw new IllegalArgumentException("Unknown state: " + state);
        }

        System.out.println("[DEBUG] Total owner bookings found: " + bookings.size());
        List<BookingDtoResponse> responseList = bookings.stream()
                .skip(from)
                .limit(size)
                .map(booking -> {
                    System.out.println("[DEBUG] Mapping owner booking: " + booking);
                    return BookingMapper.toDto(booking);
                })
                .collect(Collectors.toList());
        System.out.println("[DEBUG] Returning " + responseList.size() + " owner bookings");
        return responseList;
    }
}