package ru.yandex.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.WAITING;

    public Booking() {
        System.out.println("New booking created");
    }

    public Booking(Long id, LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        if (start.isAfter(end)) {
            System.out.println("Error: Start date is after end date");
        }
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.status = status;
    }

    public void printDetails() {
        System.out.println("Booking details: " + id + ", " + start + " to " + end);
    }
}
