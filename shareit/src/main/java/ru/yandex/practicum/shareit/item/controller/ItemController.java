package ru.yandex.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareit.item.dto.CommentResponse;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.mapper.CommentMapper;
import ru.yandex.practicum.shareit.item.mapper.ItemMapper;
import ru.yandex.practicum.shareit.item.model.Comment;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 1. Добавление новой вещи
    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = ItemMapper.toEntity(itemDto);
        Item createdItem = itemService.addItem(item, userId);
        return ItemMapper.toDto(createdItem);
    }

    // 2. Редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemDto editItem(@PathVariable long itemId,
                            @RequestBody ItemDto itemDto,
                            @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = ItemMapper.toEntity(itemDto);
        Item updatedItem = itemService.editItem(itemId, item, userId);
        return ItemMapper.toDto(updatedItem);
    }

    // 3. Просмотр информации о вещи
    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        Item item = itemService.getItem(itemId);
        return ItemMapper.toDto(item);
    }

    // 4. Просмотр списка вещей владельцем
    @GetMapping()
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByOwner(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    // 5. Поиск вещей по тексту
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam("text") String text,
            @PathVariable long itemId) {
        Comment comment = itemService.addComment(itemId, text, userId);
        return CommentMapper.toCommentResponse(comment); // Добавлено преобразование
    }

//    // 6. Получение всех вещей
//    @GetMapping
//    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
//        return itemService.getAllItems().stream()
//                .map(ItemMapper::toDto)
//                .collect(Collectors.toList());
//    }
}
