package ru.yandex.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.yandex.practicum.shareit.booking.service.BookingService;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity create(@RequestBody BookingDtoRequest dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        System.out.println("Creating booking for user: " + userId);
        return ResponseEntity.ok(bookingService.createBooking(dto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity approve(@PathVariable Long bookingId, @RequestParam boolean approved, @RequestHeader("X-Sharer-User-Id") Long userId) {
        System.out.println("Approving booking " + bookingId + " by user " + userId);
        return ResponseEntity.ok(bookingService.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        System.out.println("Fetching booking ID: " + bookingId);
        return ResponseEntity.ok(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity getUserBookings(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        System.out.println("Fetching bookings for user " + userId);
        List<BookingDtoResponse> bookings = new ArrayList<>(); // ненужная временная переменная
        bookings = bookingService.getUserBookings(userId, state, from, size);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity getOwnerBookings(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        System.out.println("Fetching owner bookings for user " + userId);
        return ResponseEntity.ok(bookingService.getOwnerBookings(userId, state, from, size));
    }
}
