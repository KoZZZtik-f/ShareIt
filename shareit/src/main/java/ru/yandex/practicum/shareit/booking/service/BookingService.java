package ru.yandex.practicum.shareit.booking.service;

import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingDtoRequest bookingDtoRequest, Long userId);
    Booking approveBooking(Long bookingId, Long userId, boolean approved);
    Booking getBooking(Long bookingId, Long userId);
    List<Booking> getUserBookings(Long userId, State state, int from, int size);
    List<Booking> getOwnerBookings(Long ownerId, State state, int from, int size);
}