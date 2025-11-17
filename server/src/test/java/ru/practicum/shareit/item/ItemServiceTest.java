package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
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
class ItemServiceTest {
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
    void createItem_shouldCreateItem() {
        User user = new User(1L, "Owner", "owner@email.com");
        ItemCreateDto createDto = new ItemCreateDto("Drill", "Powerful drill", true, null);
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(1L, createDto);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Drill"));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_shouldUpdateItem() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated Drill", null, null);
        Item updatedItem = new Item(1L, "Updated Drill", "Powerful drill", true, 1L, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertThat(result.getName(), equalTo("Updated Drill"));
    }

    @Test
    void updateItem_shouldThrowException_whenNotOwner() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated Drill", null, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(1L, 1L, updateDto));
    }

    @Test
    void getItem_shouldReturnItem() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItem_Id(1L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemId(anyLong(), any())).thenReturn(Collections.emptyList());

        ItemWithBookingsDto result = itemService.getItem(1L, 1L);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Drill"));
    }

    @Test
    void searchItems_shouldReturnEmptyList_whenTextIsBlank() {
        List<ItemDto> result = itemService.searchItems("");

        assertThat(result, empty());
        verify(itemRepository, never()).search(anyString());
    }

    @Test
    void searchItems_shouldReturnItems() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);

        when(itemRepository.search("drill")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.searchItems("drill");

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getName(), equalTo("Drill"));
    }

    @Test
    void addComment_shouldAddComment() {
        User user = new User(1L, "User", "user@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Comment comment = new Comment(1L, "Great item!", item, user, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsCompletedBookingByBookerAndItem(anyLong(), anyLong(), any()))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, commentDto);

        assertThat(result.getText(), equalTo("Great item!"));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowException_whenNoCompletedBooking() {
        User user = new User(1L, "User", "user@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsCompletedBookingByBookerAndItem(anyLong(), anyLong(), any()))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> itemService.addComment(1L, 1L, commentDto));
    }
}