package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void createBooking_shouldCreateBooking() {
        User owner = new User(null, "Owner", "owner@email.com");
        owner = userRepository.save(owner);

        User booker = new User(null, "Booker", "booker@email.com");
        booker = userRepository.save(booker);

        Item item = new Item(null, "Drill", "Powerful drill", true, owner.getId(), null);
        item = itemRepository.save(item);

        BookingCreateDto createDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        BookingDto result = bookingService.createBooking(booker.getId(), createDto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(result.getItem().getId(), equalTo(item.getId()));
        assertThat(result.getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    void getUserBookings_shouldReturnBookings() {
        User owner = new User(null, "Owner", "owner@email.com");
        owner = userRepository.save(owner);

        User booker = new User(null, "Booker", "booker@email.com");
        booker = userRepository.save(booker);

        Item item = new Item(null, "Drill", "Powerful drill", true, owner.getId(), null);
        item = itemRepository.save(item);

        BookingCreateDto createDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        bookingService.createBooking(booker.getId(), createDto);

        List<BookingDto> results = bookingService.getUserBookings(booker.getId(), BookingState.ALL);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getBooker().getId(), equalTo(booker.getId()));
    }
}