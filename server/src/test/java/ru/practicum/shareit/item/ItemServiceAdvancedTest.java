package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceAdvancedTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void updateItem_shouldUpdateOnlyName() {
        Item item = new Item(1L, "Old Name", "Old Description", true, 1L, null);
        ItemUpdateDto updateDto = new ItemUpdateDto("New Name", null, null);
        Item updatedItem = new Item(1L, "New Name", "Old Description", true, 1L, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        itemService.updateItem(1L, 1L, updateDto);
    }

    @Test
    void updateItem_shouldUpdateOnlyDescription() {
        Item item = new Item(1L, "Name", "Old Description", true, 1L, null);
        ItemUpdateDto updateDto = new ItemUpdateDto(null, "New Description", null);
        Item updatedItem = new Item(1L, "Name", "New Description", true, 1L, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        itemService.updateItem(1L, 1L, updateDto);
    }

    @Test
    void updateItem_shouldUpdateOnlyAvailable() {
        Item item = new Item(1L, "Name", "Description", true, 1L, null);
        ItemUpdateDto updateDto = new ItemUpdateDto(null, null, false);
        Item updatedItem = new Item(1L, "Name", "Description", false, 1L, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        itemService.updateItem(1L, 1L, updateDto);
    }

    @Test
    void getItem_shouldReturnItemWithBookings_whenOwner() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);
        User booker = new User(2L, "Booker", "booker@email.com");

        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItem_Id(1L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(nextBooking, lastBooking));

        ItemWithBookingsDto result = itemService.getItem(1L, 1L);

        assertThat(result.getLastBooking(), notNullValue());
        assertThat(result.getNextBooking(), notNullValue());
        assertThat(result.getLastBooking().getId(), equalTo(1L));
        assertThat(result.getNextBooking().getId(), equalTo(2L));
    }

    @Test
    void getItem_shouldNotReturnBookings_whenNotOwner() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItem_Id(1L)).thenReturn(Collections.emptyList());

        ItemWithBookingsDto result = itemService.getItem(1L, 1L);

        assertThat(result.getLastBooking(), nullValue());
        assertThat(result.getNextBooking(), nullValue());
    }

    @Test
    void getItemsByOwner_shouldReturnItemsWithBookings() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);
        User booker = new User(2L, "Booker", "booker@email.com");

        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        when(itemRepository.findAllByOwner(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(nextBooking, lastBooking));
        when(commentRepository.findByItem_IdIn(any())).thenReturn(Collections.emptyList());

        List<ItemWithBookingsDto> result = itemService.getItemsByOwner(1L);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getLastBooking(), notNullValue());
        assertThat(result.get(0).getNextBooking(), notNullValue());
    }

    @Test
    void getItemsByOwner_shouldReturnEmptyList_whenNoItems() {
        when(itemRepository.findAllByOwner(1L)).thenReturn(Collections.emptyList());

        List<ItemWithBookingsDto> result = itemService.getItemsByOwner(1L);

        assertThat(result, empty());
    }

    @Test
    void searchItems_shouldReturnEmptyList_whenTextIsNull() {
        List<ItemDto> result = itemService.searchItems(null);

        assertThat(result, empty());
    }

    @Test
    void searchItems_shouldReturnEmptyList_whenTextIsWhitespace() {
        List<ItemDto> result = itemService.searchItems("   ");

        assertThat(result, empty());
    }

    @Test
    void getItemsByOwner_shouldHandleRejectedBookings() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);
        User booker = new User(2L, "Booker", "booker@email.com");

        Booking rejectedBooking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, BookingStatus.REJECTED);
        Booking approvedBooking = new Booking(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        when(itemRepository.findAllByOwner(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(approvedBooking, rejectedBooking));
        when(commentRepository.findByItem_IdIn(any())).thenReturn(Collections.emptyList());

        List<ItemWithBookingsDto> result = itemService.getItemsByOwner(1L);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getLastBooking(), nullValue());
        assertThat(result.get(0).getNextBooking(), notNullValue());
    }
}