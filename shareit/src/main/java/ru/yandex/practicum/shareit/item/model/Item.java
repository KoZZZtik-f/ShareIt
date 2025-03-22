package ru.yandex.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.user.model.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Availability cannot be null")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "item")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "item")
    private List<Comment> comments;

    // Конструктор без ID и bookings
    public Item(String name, String description, Boolean available, User owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
