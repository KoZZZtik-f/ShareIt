package ru.yandex.practicum.shareit.item.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.exception.PermissionDeniedException;
import ru.yandex.practicum.shareit.item.exception.ItemNotFoundException;
import ru.yandex.practicum.shareit.item.model.Comment;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.item.repository.CommentRepository;
import ru.yandex.practicum.shareit.item.repository.ItemRepository;
import ru.yandex.practicum.shareit.user.model.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Override
    public Item addItem(Item item, long userId) {
        User owner = userService.getUserById(userId);
        item.setOwner(owner); // Устанавливаем владельца
        return itemRepository.save(item);
    }

    @Override
    public Item editItem(long itemId, Item updatedItem, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException());

        // Проверяем, является ли пользователь владельцем
        if (!item.getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("User with id " + userId + " is not the owner of item " + itemId);
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
                .orElseThrow(() -> new ItemNotFoundException());
    }

    @Override
    public List<Item> getItemsByOwner(long userId) {
        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Comment addComment(long itemId, String text, long userId) {
        Item item = getItem(itemId); // Получаем вещь по ID
        User author = userService.getUserById(userId); // Получаем пользователя по ID

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }


//    @Override
//    public List<Item> getAllItems() {
//        return itemRepository.findAll();
//    }


}