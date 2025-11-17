package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceAdvancedTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_shouldThrowException_whenEndBeforeStart() {
        BookingCreateDto createDto = new BookingCreateDto(
                1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, createDto));
    }

    @Test
    void createBooking_shouldThrowException_whenEndEqualsStart() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        BookingCreateDto createDto = new BookingCreateDto(1L, time, time);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, createDto));
    }

    @Test
    void updateBookingStatus_shouldThrowException_whenAlreadyProcessed() {
        User owner = new User(2L, "Owner", "owner@email.com");
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(2L, 1L, true));
    }

    @Test
    void getUserBookings_shouldReturnCurrentBookings() {
        User user = new User(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentBookingsByBooker(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getUserBookings(1L, BookingState.CURRENT);
    }

    @Test
    void getUserBookings_shouldReturnPastBookings() {
        User user = new User(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getUserBookings(1L, BookingState.PAST);
    }

    @Test
    void getUserBookings_shouldReturnFutureBookings() {
        User user = new User(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getUserBookings(1L, BookingState.FUTURE);
    }

    @Test
    void getUserBookings_shouldReturnWaitingBookings() {
        User user = new User(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getUserBookings(1L, BookingState.WAITING);
    }

    @Test
    void getUserBookings_shouldReturnRejectedBookings() {
        User user = new User(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getUserBookings(1L, BookingState.REJECTED);
    }

    @Test
    void getOwnerBookings_shouldReturnCurrentBookings() {
        User user = new User(1L, "Owner", "owner@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentBookingsByOwner(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getOwnerBookings(1L, BookingState.CURRENT);
    }

    @Test
    void getOwnerBookings_shouldReturnPastBookings() {
        User user = new User(1L, "Owner", "owner@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getOwnerBookings(1L, BookingState.PAST);
    }

    @Test
    void getOwnerBookings_shouldReturnFutureBookings() {
        User user = new User(1L, "Owner", "owner@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getOwnerBookings(1L, BookingState.FUTURE);
    }

    @Test
    void getOwnerBookings_shouldReturnWaitingBookings() {
        User user = new User(1L, "Owner", "owner@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getOwnerBookings(1L, BookingState.WAITING);
    }

    @Test
    void getOwnerBookings_shouldReturnRejectedBookings() {
        User user = new User(1L, "Owner", "owner@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getOwnerBookings(1L, BookingState.REJECTED);
    }
}