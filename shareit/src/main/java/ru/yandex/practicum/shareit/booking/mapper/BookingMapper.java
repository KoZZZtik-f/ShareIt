package ru.yandex.practicum.shareit.booking.mapper;

import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.BookingStatus;
import ru.yandex.practicum.shareit.item.mapper.ItemMapper;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.user.mapper.UserMapper;
import ru.yandex.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toEntity(BookingDtoRequest dto, Item item, User booker) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDtoResponse toDto(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.toDto(booking.getItem())) // Используем ItemMapper
                .booker(UserMapper.toDto(booking.getBooker())) // Используем UserMapper
                .build();
    }
}