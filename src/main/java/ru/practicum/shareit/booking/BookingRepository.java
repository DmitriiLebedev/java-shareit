package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select new ru.practicum.shareit.booking.BookingForItem(b.id, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.start > ?2 " +
            "and b.status = ?3 " +
            "order by b.start asc")
    List<BookingForItem> findNextBookingForItem(Long itemId, LocalDateTime currentTime, BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.BookingForItem(b.id, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.start < ?2 " +
            "and b.status = ?3 " +
            "order by b.start desc")
    List<BookingForItem> findLastBookingForItem(Long itemId, LocalDateTime currentTime, BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and (?2 between b.start and b.end) " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdCurrentBookings(Long userId, LocalDateTime now);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "and (?2 between b.start and b.end) " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdCurrentBookings(Long ownerId, LocalDateTime now);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findALLByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);
}