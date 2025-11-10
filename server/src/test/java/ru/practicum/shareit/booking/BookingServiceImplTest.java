package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@example.com");
        booker = new User(2L, "Booker", "booker@example.com");
        item = new Item(1L, "Item", "Description", true, 1L, null);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        bookingCreateDto = new BookingCreateDto(1L, start, end);
    }

    @Test
    void createBooking_shouldCreateBooking_whenDataIsValid() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(2L, bookingCreateDto);

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowIllegalArgumentException_whenEndBeforeStart() {
        BookingCreateDto invalidDto = new BookingCreateDto(
                1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(2L, invalidDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowNotFoundException_whenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(2L, bookingCreateDto));
    }

    @Test
    void createBooking_shouldThrowNotFoundException_whenItemNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(2L, bookingCreateDto));
    }

    @Test
    void createBooking_shouldThrowIllegalArgumentException_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(2L, bookingCreateDto));
    }

    @Test
    void createBooking_shouldThrowForbiddenException_whenOwnerTriesToBook() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class,
                () -> bookingService.createBooking(1L, bookingCreateDto));
    }

    @Test
    void updateBookingStatus_shouldApproveBooking_whenOwnerApproves() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking approvedBooking = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                BookingStatus.APPROVED
        );
        when(bookingRepository.save(any(Booking.class))).thenReturn(approvedBooking);

        BookingDto result = bookingService.updateBookingStatus(1L, 1L, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_shouldRejectBooking_whenOwnerRejects() {
        Booking waitingBooking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.WAITING
        );

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(waitingBooking));

        Booking rejectedBooking = new Booking(
                1L,
                waitingBooking.getStart(),
                waitingBooking.getEnd(),
                item,
                booker,
                BookingStatus.REJECTED
        );
        when(bookingRepository.save(any(Booking.class))).thenReturn(rejectedBooking);

        BookingDto result = bookingService.updateBookingStatus(1L, 1L, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_shouldThrowForbiddenException_whenNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class,
                () -> bookingService.updateBookingStatus(2L, 1L, true));
    }

    @Test
    void updateBookingStatus_shouldThrowIllegalArgumentException_whenAlreadyProcessed() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(1L, 1L, true));
    }

    @Test
    void getBooking_shouldReturnBooking_whenUserIsBookerOrOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(2L, 1L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBooking_shouldThrowForbiddenException_whenUserHasNoAccess() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class,
                () -> bookingService.getBooking(3L, 1L));
    }

    @Test
    void getUserBookings_shouldReturnAllBookings_whenStateIsAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getUserBookings(2L, BookingState.ALL);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_shouldThrowNotFoundException_whenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getUserBookings(2L, BookingState.ALL));
    }

    @Test
    void getOwnerBookings_shouldReturnBookings_whenOwnerExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwner(anyLong(), any(Sort.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getOwnerBookings(1L, BookingState.ALL);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}