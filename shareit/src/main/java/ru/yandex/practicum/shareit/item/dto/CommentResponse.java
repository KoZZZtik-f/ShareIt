package ru.yandex.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id; // ID комментария
    private String text; // Текст комментария
    private String authorName; // Имя автора
    private LocalDateTime created; // Дата создания
}