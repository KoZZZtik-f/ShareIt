package ru.yandex.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.item.repository.ItemRepository;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        // Создаем новый объект Item
        Item item = new Item(itemDto.getName(), itemDto.getDescription(), itemDto.isAvailable(), userId);
        // Сохраняем в репозитории
        item = itemRepository.save(item);

        // Можно добавить дополнительные операции, например, обновление других сущностей

        // Возвращаем ItemDto с данными сохраненной вещи
        return new ItemDto(item.getName(), item.getDescription(), item.isAvailable());
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto, long userId) {
        Item item = itemRepository.findById(itemId);

        // Проверка, что пользователь является владельцем
        if (item.getOwnerId() != userId) {
            throw new IllegalArgumentException("User is not the owner of the item");
        }

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.isAvailable());

        itemRepository.save(item);

        return itemDto;
    }

    @Override
    public ItemDto getItem(long itemId) {
        Item item = itemRepository.findById(itemId);
        return new ItemDto(item.getName(), item.getDescription(), item.isAvailable());
    }

    @Override
    public List<ItemDto> getItemsByOwner(long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream()
                .map(item -> new ItemDto(item.getName(), item.getDescription(), item.isAvailable()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items = itemRepository.findByContainingText(text, text);
        return items.stream()
                .map(item -> new ItemDto(item.getName(), item.getDescription(), item.isAvailable()))
                .collect(Collectors.toList());
    }
}
