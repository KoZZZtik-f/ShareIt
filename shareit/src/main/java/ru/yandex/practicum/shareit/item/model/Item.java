package ru.yandex.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Availability cannot be null")
    private boolean available;

    @Column(name = "owner_id")
    private long ownerId;

    // Конструктор без ID
    public Item(String name, String description, boolean available, long ownerId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
    }
}
