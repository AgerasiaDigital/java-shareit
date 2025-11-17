package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class BookingTest {

    @Test
    void testBookingCreation() {
        User user = new User(1L, "User", "user@email.com");
        Item item = new Item(1L, "Item", "Description", true, 2L, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        assertThat(booking.getId(), equalTo(1L));
        assertThat(booking.getStart(), equalTo(start));
        assertThat(booking.getEnd(), equalTo(end));
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getBooker(), equalTo(user));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void testBookingSetters() {
        Booking booking = new Booking();
        User user = new User(1L, "User", "user@email.com");
        Item item = new Item(1L, "Item", "Description", true, 2L, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        assertThat(booking.getId(), equalTo(1L));
        assertThat(booking.getStart(), equalTo(start));
        assertThat(booking.getEnd(), equalTo(end));
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getBooker(), equalTo(user));
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }
}