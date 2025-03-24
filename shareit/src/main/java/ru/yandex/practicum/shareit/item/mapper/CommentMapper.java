package ru.yandex.practicum.shareit.item.mapper;

import ru.yandex.practicum.shareit.item.dto.CommentResponse;
import ru.yandex.practicum.shareit.item.model.Comment;

public class CommentMapper {

    public static CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

}
