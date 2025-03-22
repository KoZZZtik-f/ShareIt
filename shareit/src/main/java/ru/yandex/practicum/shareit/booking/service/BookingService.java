package ru.yandex.practicum.shareit.booking.service;

import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.yandex.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, Long userId);
    BookingDtoResponse approveBooking(Long bookingId, Long userId, boolean approved);
    BookingDtoResponse getBooking(Long bookingId, Long userId);
    List<BookingDtoResponse> getUserBookings(Long userId, State state, int from, int size);
    List<BookingDtoResponse> getOwnerBookings(Long ownerId, State state, int from, int size);
}