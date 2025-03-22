package ru.yandex.practicum.shareit.item.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareit.item.repository.ItemRepository;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.mapper.ItemMapper;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item addItem(Item item, long userId) {
        User owner = userService.getUserById(userId);
        item.setOwner(owner); // Устанавливаем владельца
        return itemRepository.save(item); // Возвращаем сущность
    }

    @Override
    public Item editItem(long itemId, Item updatedItem, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner");
        }

        // Обновляем только переданные поля
        if (updatedItem.getName() != null) item.setName(updatedItem.getName());
        if (updatedItem.getDescription() != null) item.setDescription(updatedItem.getDescription());
        if (updatedItem.getAvailable() != null) item.setAvailable(updatedItem.getAvailable());

        return itemRepository.save(item); // Возвращаем сущность
    }

    @Override
    public Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
    }

    @Override
    public List<Item> getItemsByOwner(long userId) {
        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}