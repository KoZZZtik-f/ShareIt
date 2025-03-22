package ru.yandex.practicum.shareit.item.mapper;

import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toEntity(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null // Владелец устанавливается в сервисе
        );
    }
}