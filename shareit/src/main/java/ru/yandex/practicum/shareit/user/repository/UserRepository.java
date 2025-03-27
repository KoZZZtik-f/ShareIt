package ru.yandex.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    // метод для проверки email с исключением текущего пользователя
    boolean existsByEmailAndIdNot(String email, Long id);

    // кастомный метод
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM User u WHERE u.id = :id")
    int deleteUserById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id IN :ids")
    int deleteAllByIds(@Param("ids") List<Long> ids);

    Long id(Long id);
}
