package ru.yandex.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shareit.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
