package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
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

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and (?2 between b.start and b.end) " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdCurrentBookings(Long userId, LocalDateTime now, PageRequest page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long ownerId, PageRequest page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now, PageRequest page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now, PageRequest page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "and (?2 between b.start and b.end) " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdCurrentBookings(Long ownerId, LocalDateTime now, PageRequest page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerId = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, PageRequest page);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, PageRequest page);

    List<Booking> findALLByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status, PageRequest page);

    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now, PageRequest page);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now, PageRequest page);
}