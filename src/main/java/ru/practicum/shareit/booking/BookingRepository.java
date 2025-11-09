package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2")
    List<Booking> findCurrentBookingsByBooker(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    // Бронирования для вещей владельца
    List<Booking> findByItemOwner(Long ownerId, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2")
    List<Booking> findCurrentBookingsByOwner(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemId(Long itemId, Sort sort);

    @Query("select case when count(b) > 0 then true else false end " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.item.id = ?2 " +
            "and b.end < ?3 " +
            "and b.status = 'APPROVED'")
    boolean existsCompletedBookingByBookerAndItem(Long bookerId, Long itemId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status != 'REJECTED' " +
            "and b.status != 'CANCELED' " +
            "and ((b.start < ?3 and b.end > ?2) " +
            "or (b.start >= ?2 and b.start < ?3))")
    List<Booking> findOverlappingBookings(Long itemId, LocalDateTime start, LocalDateTime end);
}