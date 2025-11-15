package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_shouldCreateBooking() {
        User user = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        BookingCreateDto createDto = new BookingCreateDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        Booking booking = new Booking(1L, createDto.getStart(), createDto.getEnd(),
                item, user, BookingStatus.WAITING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(1L, createDto);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowException_whenItemNotAvailable() {
        User user = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", false, 2L, null);
        BookingCreateDto createDto = new BookingCreateDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, createDto));
    }

    @Test
    void createBooking_shouldThrowException_whenOwnerTriesToBook() {
        User user = new User(1L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);
        BookingCreateDto createDto = new BookingCreateDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class,
                () -> bookingService.createBooking(1L, createDto));
    }

    @Test
    void updateBookingStatus_shouldUpdateStatus() {
        User owner = new User(2L, "Owner", "owner@email.com");
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.updateBookingStatus(2L, 1L, true);

        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_shouldThrowException_whenNotOwner() {
        User owner = new User(2L, "Owner", "owner@email.com");
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class,
                () -> bookingService.updateBookingStatus(3L, 1L, true));
    }

    @Test
    void getBooking_shouldReturnBooking() {
        User owner = new User(2L, "Owner", "owner@email.com");
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(1L, 1L);

        assertThat(result.getId(), equalTo(1L));
    }

    @Test
    void getUserBookings_shouldReturnAllBookings() {
        User user = new User(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(anyLong(), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingService.getUserBookings(1L, BookingState.ALL);

        assertThat(result, empty());
        verify(bookingRepository, times(1)).findByBookerId(anyLong(), any(Sort.class));
    }

    @Test
    void getOwnerBookings_shouldReturnAllBookings() {
        User user = new User(1L, "Owner", "owner@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwner(anyLong(), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingService.getOwnerBookings(1L, BookingState.ALL);

        assertThat(result, empty());
        verify(bookingRepository, times(1)).findByItemOwner(anyLong(), any(Sort.class));
    }
}