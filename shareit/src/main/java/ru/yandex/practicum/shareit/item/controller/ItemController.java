package ru.yandex.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 1. Добавление новой вещи
    @PostMapping
    public void addItem(@RequestBody ItemDto itemDto,
                        @RequestHeader("X-Shared-User-ID") long userId) {
        itemService.addItem(itemDto, userId);
    }

    // 2. Редактирование вещи
    @PatchMapping("/{itemId}")
    public void editItem(@PathVariable long itemId,
                         @RequestBody ItemDto itemDto,
                         @RequestHeader("X-Shared-User-ID") long userId) {
        itemService.editItem(itemId, itemDto, userId);
    }

    // 3. Просмотр информации о вещи
    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    // 4. Просмотр списка вещей владельцем
    @GetMapping("/owner")
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Shared-User-ID") long userId) {
        return itemService.getItemsByOwner(userId);
    }

    // 5. Поиск вещей по тексту
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItems(text);
    }
}
