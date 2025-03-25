package ru.yandex.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.yandex.practicum.shareit.booking.exception.UnsupportedStateException;
import ru.yandex.practicum.shareit.booking.mapper.BookingMapper;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.State;
import ru.yandex.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestBody BookingDtoRequest bookingDtoRequest,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingService.createBooking(bookingDtoRequest, userId);
        return BookingMapper.toDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approveBooking(
            @PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingService.approveBooking(bookingId, userId, approved);
        return BookingMapper.toDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingService.getBooking(bookingId, userId);
        return BookingMapper.toDto(booking);
    }

    @GetMapping
    public List<BookingDtoResponse> getUserBookings(
            @RequestParam(value = "state", defaultValue = "ALL") String stateStr,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        State state = convertToStateWithChecking(stateStr);
        List<Booking> bookings = bookingService.getUserBookings(userId, state, from, size);

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBookings(
            @RequestParam(value = "state", defaultValue = "ALL") String stateStr,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        State state = convertToStateWithChecking(stateStr);
        List<Booking> bookings = bookingService.getOwnerBookings(userId, state, from, size);

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }


    private static State convertToStateWithChecking(String stateStr) {
        State state;

        try {
            state = State.valueOf(stateStr);
            System.out.println(String.format("Debug %s -> %s", stateStr, state.toString()));
        } catch (RuntimeException e) {
            throw new UnsupportedStateException(stateStr);
        }

        return state;
    }
}