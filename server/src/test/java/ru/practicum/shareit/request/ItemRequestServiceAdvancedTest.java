package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceAdvancedTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createRequest_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(1L, null));
    }

    @Test
    void getUserRequests_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequests(1L));
    }

    @Test
    void getUserRequests_shouldReturnRequestsWithItems() {
        User user = new User(1L, "User", "user@email.com");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(any())).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getItems(), hasSize(1));
        assertThat(result.get(0).getItems().get(0).getName(), equalTo("Drill"));
    }

    @Test
    void getAllRequests_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(1L, 0, 10));
    }

    @Test
    void getAllRequests_shouldReturnRequestsWithItems() {
        User user1 = new User(1L, "User 1", "user1@email.com");
        User user2 = new User(2L, "User 2", "user2@email.com");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user2, LocalDateTime.now());
        Item item = new Item(1L, "Drill", "Powerful drill", true, 3L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findByRequestorIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(any())).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, 0, 10);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getItems(), hasSize(1));
    }

    @Test
    void getRequest_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequest(1L, 1L));
    }

    @Test
    void getRequest_shouldReturnRequestWithMultipleItems() {
        User user = new User(1L, "User", "user@email.com");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());
        Item item1 = new Item(1L, "Drill 1", "Description 1", true, 2L, 1L);
        Item item2 = new Item(2L, "Drill 2", "Description 2", true, 3L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest(1L)).thenReturn(List.of(item1, item2));

        ItemRequestDto result = itemRequestService.getRequest(1L, 1L);

        assertThat(result.getItems(), hasSize(2));
        assertThat(result.getItems().get(0).getName(), equalTo("Drill 1"));
        assertThat(result.getItems().get(1).getName(), equalTo("Drill 2"));
    }
}