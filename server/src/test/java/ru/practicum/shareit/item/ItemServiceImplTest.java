package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
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
class ItemServiceImplTest {

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

    private User user;
    private Item item;
    private ItemCreateDto itemCreateDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "test@example.com");
        item = new Item(1L, "Item Name", "Item Description", true, 1L, null);
        itemCreateDto = new ItemCreateDto("Item Name", "Item Description", true, null);
    }

    @Test
    void createItem_shouldCreateItem_whenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(1L, itemCreateDto);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowNotFoundException_whenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, itemCreateDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldUpdateItem_whenUserIsOwner() {
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated Name", "Updated Description", false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemNotExists() {
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated Name", null, null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, updateDto));
    }

    @Test
    void updateItem_shouldThrowForbiddenException_whenUserIsNotOwner() {
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated Name", null, null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> itemService.updateItem(2L, 1L, updateDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItem_shouldReturnItem_whenItemExists() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItem_Id(anyLong())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemId(anyLong(), any())).thenReturn(Collections.emptyList());

        ItemWithBookingsDto result = itemService.getItem(1L, 1L);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void getItem_shouldThrowNotFoundException_whenItemNotExists() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
    }

    @Test
    void searchItems_shouldReturnEmptyList_whenTextIsBlank() {
        List<ItemDto> result = itemService.searchItems("");

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(anyString());
    }

    @Test
    void searchItems_shouldReturnItems_whenTextIsNotBlank() {
        when(itemRepository.search(anyString())).thenReturn(List.of(item));

        List<ItemDto> result = itemService.searchItems("test");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(itemRepository).search("test");
    }

    @Test
    void addComment_shouldAddComment_whenUserHasCompletedBooking() {
        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);
        Comment comment = new Comment(1L, "Great item!", item, user, LocalDateTime.now());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsCompletedBookingByBookerAndItem(anyLong(), anyLong(), any()))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, commentDto);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowIllegalArgumentException_whenNoCompletedBooking() {
        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsCompletedBookingByBookerAndItem(anyLong(), anyLong(), any()))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> itemService.addComment(1L, 1L, commentDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getItemsByOwner_shouldReturnItems_whenOwnerHasItems() {
        when(itemRepository.findAllByOwner(anyLong())).thenReturn(List.of(item));
        when(commentRepository.findByItem_IdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemWithBookingsDto> result = itemService.getItemsByOwner(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getItemsByOwner_shouldReturnEmptyList_whenOwnerHasNoItems() {
        when(itemRepository.findAllByOwner(anyLong())).thenReturn(Collections.emptyList());

        List<ItemWithBookingsDto> result = itemService.getItemsByOwner(1L);

        assertTrue(result.isEmpty());
    }
}