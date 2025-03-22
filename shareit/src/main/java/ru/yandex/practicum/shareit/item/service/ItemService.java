package ru.yandex.practicum.shareit.item.service;

import ru.yandex.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemService {
    Item addItem(Item item, long userId); // Изменено: Item вместо ItemDto
    Item editItem(long itemId, Item item, long userId); // Изменено
    Item getItem(long itemId);
    List<Item> getItemsByOwner(long userId); // Изменено
    List<Item> searchItems(String text); // Изменено
}