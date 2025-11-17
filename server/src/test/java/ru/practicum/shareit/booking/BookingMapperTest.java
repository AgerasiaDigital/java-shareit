package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BookingMapperTest {

    @Test
    void toBookingDto_shouldMapCorrectly() {
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        BookingDto result = BookingMapper.toBookingDto(booking);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getStart(), equalTo(start));
        assertThat(result.getEnd(), equalTo(end));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(result.getBooker().getId(), equalTo(1L));
        assertThat(result.getItem().getId(), equalTo(1L));
        assertThat(result.getItem().getName(), equalTo("Drill"));
    }

    @Test
    void toBooking_shouldMapCorrectly() {
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto dto = new BookingCreateDto(1L, start, end);

        Booking result = BookingMapper.toBooking(dto, item, booker);

        assertThat(result.getStart(), equalTo(start));
        assertThat(result.getEnd(), equalTo(end));
        assertThat(result.getItem(), equalTo(item));
        assertThat(result.getBooker(), equalTo(booker));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
    }
}