package ru.yandex.practicum.shareit.item.service;

import ru.yandex.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    void addItem(ItemDto itemDto, long userId);
    void editItem(long itemId, ItemDto itemDto, long userId);
    ItemDto getItem(long itemId);
    List<ItemDto> getItemsByOwner(long userId);
    List<ItemDto> searchItems(String text);
}
