package ru.yandex.practicum.shareit.booking.mapper;

import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.BookingStatus;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.user.dto.UserDto;
import ru.yandex.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toEntity(BookingDtoRequest dto, Item item, User booker) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDtoResponse toDto(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(toItemDto(booking.getItem())) // Маппинг Item в ItemDto
                .build();
    }

    private static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    private static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName()
        );
    }
}