package ru.yandex.practicum.shareit.item.mapper;

import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());

        if (item.getComments() != null) {
            dto.setComments(item.getComments().stream()
                    .map(CommentMapper::toCommentResponse)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static Item toEntity(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null
        );
    }
}