package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(BookingServiceImpl.class)
class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final TestEntityManager entityManager;

    @Test
    void createBooking_shouldCreateBooking() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        entityManager.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        entityManager.persist(booker);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        entityManager.persist(item);
        entityManager.flush();

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
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        entityManager.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        entityManager.persist(booker);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        entityManager.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);
        entityManager.flush();

        List<BookingDto> results = bookingService.getUserBookings(booker.getId(), BookingState.ALL);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getBooker().getId(), equalTo(booker.getId()));
    }
}