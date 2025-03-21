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

//    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User owner = userService.getUserById(userId);
        Item item = ItemMapper.toEntity(itemDto, owner);
        return ItemMapper.toDto(itemRepository.save(item));
    }

//    @Transactional
    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id " + itemId));

        if (!item.getOwner().getId().equals(userId)) {
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
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
