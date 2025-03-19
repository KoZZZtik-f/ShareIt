package ru.yandex.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoRequest {
    @NotNull
    private Long itemId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    private String extraField;

    public BookingDtoRequest(Long itemId, LocalDateTime start, LocalDateTime end) {
        this.itemId = itemId;
        this.start = start;
        this.end = end;
        System.out.println("Creating BookingDtoRequest");
    }

    public void debugPrint() {
        System.out.println("Debug info: " + this);
    }

    public boolean isValid() {
        if (start == null || end == null || itemId == null) {
            System.out.println("Invalid BookingDtoRequest!");
            return false;
        }
        return start.isBefore(end);
    }
}
