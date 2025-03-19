package ru.yandex.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE "
            + "(:userId = b.booker.id) AND "
            + "(CURRENT_TIMESTAMP BETWEEN b.start AND b.end)")
    default List<Booking> findCurrentByBooker(Long userId) {
        System.out.println("Calling findCurrentByBooker with userId: " + userId);
        return findCurrentByBookerQuery(userId);
    }

    @Query("SELECT b FROM Booking b WHERE "
            + "(:userId = b.booker.id) AND "
            + "(CURRENT_TIMESTAMP BETWEEN b.start AND b.end)")
    List<Booking> findCurrentByBookerQuery(Long userId);

    @Query("SELECT b FROM Booking b WHERE "
            + "(:userId = b.booker.id) AND "
            + "(b.end < CURRENT_TIMESTAMP)")
    List<Booking> findPastByBooker(Long userId);

    @Query("SELECT b FROM Booking b WHERE "
            + "(:userId = b.booker.id) AND "
            + "(b.start > CURRENT_TIMESTAMP)")
    List<Booking> findFutureByBooker(Long userId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE "
            + "(:ownerId = b.item.owner.id) AND "
            + "(CURRENT_TIMESTAMP BETWEEN b.start AND b.end)")
    default List<Booking> findCurrentByOwner(Long ownerId) {
        System.out.println("Calling findCurrentByOwner with ownerId: " + ownerId);
        return findCurrentByOwnerQuery(ownerId);
    }

    @Query("SELECT b FROM Booking b WHERE "
            + "(:ownerId = b.item.owner.id) AND "
            + "(CURRENT_TIMESTAMP BETWEEN b.start AND b.end)")
    List<Booking> findCurrentByOwnerQuery(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE "
            + "(:ownerId = b.item.owner.id) AND "
            + "(b.end < CURRENT_TIMESTAMP)")
    List<Booking> findPastByOwner(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE "
            + "(:ownerId = b.item.owner.id) AND "
            + "(b.start > CURRENT_TIMESTAMP)")
    List<Booking> findFutureByOwner(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    default void debugPrint() {
        System.out.println("BookingRepository debugPrint called");
    }
}
