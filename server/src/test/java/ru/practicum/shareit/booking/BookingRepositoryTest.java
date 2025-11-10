package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@example.com");
        booker = new User(null, "Booker", "booker@example.com");
        em.persist(owner);
        em.persist(booker);

        item = new Item(null, "Item", "Description", true, owner.getId(), null);
        em.persist(item);
        em.flush();
    }

    @Test
    void findByBookerId_shouldReturnBookerBookings() {
        Booking booking1 = new Booking(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING);
        Booking booking2 = new Booking(null,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                item, booker, BookingStatus.APPROVED);
        em.persist(booking1);
        em.persist(booking2);
        em.flush();

        List<Booking> bookings = bookingRepository.findByBookerId(
                booker.getId(),
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertEquals(2, bookings.size());
    }

    @Test
    void existsCompletedBookingByBookerAndItem_shouldReturnTrue_whenCompletedBookingExists() {
        Booking booking = new Booking(null,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item, booker, BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        boolean exists = bookingRepository.existsCompletedBookingByBookerAndItem(
                booker.getId(),
                item.getId(),
                LocalDateTime.now()
        );

        assertTrue(exists);
    }

    @Test
    void existsCompletedBookingByBookerAndItem_shouldReturnFalse_whenNoCompletedBooking() {
        Booking booking = new Booking(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        boolean exists = bookingRepository.existsCompletedBookingByBookerAndItem(
                booker.getId(),
                item.getId(),
                LocalDateTime.now()
        );

        assertFalse(exists);
    }

    @Test
    void findOverlappingBookings_shouldReturnOverlappingBookings() {
        LocalDateTime start1 = LocalDateTime.now().plusDays(1);
        LocalDateTime end1 = LocalDateTime.now().plusDays(3);

        Booking booking = new Booking(null, start1, end1, item, booker, BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        LocalDateTime newStart = LocalDateTime.now().plusDays(2);
        LocalDateTime newEnd = LocalDateTime.now().plusDays(4);

        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                item.getId(),
                newStart,
                newEnd
        );

        assertEquals(1, overlapping.size());
    }

    @Test
    void findOverlappingBookings_shouldReturnEmpty_whenNoOverlap() {
        LocalDateTime start1 = LocalDateTime.now().plusDays(1);
        LocalDateTime end1 = LocalDateTime.now().plusDays(2);

        Booking booking = new Booking(null, start1, end1, item, booker, BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        LocalDateTime newStart = LocalDateTime.now().plusDays(3);
        LocalDateTime newEnd = LocalDateTime.now().plusDays(4);

        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                item.getId(),
                newStart,
                newEnd
        );

        assertTrue(overlapping.isEmpty());
    }

    @Test
    void findByItemOwner_shouldReturnOwnerItemBookings() {
        Booking booking = new Booking(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        List<Booking> bookings = bookingRepository.findByItemOwner(
                owner.getId(),
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertEquals(1, bookings.size());
    }
}