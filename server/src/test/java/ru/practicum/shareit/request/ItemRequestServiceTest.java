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
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createRequest_shouldCreateRequest() {
        User user = new User(1L, "User", "user@email.com");
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = itemRequestService.createRequest(1L, createDto);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getDescription(), equalTo("Need a drill"));
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getUserRequests_shouldReturnRequests() {
        User user = new User(1L, "User", "user@email.com");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(any())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDescription(), equalTo("Need a drill"));
    }

    @Test
    void getAllRequests_shouldReturnRequests() {
        User user = new User(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, 0, 10);

        assertThat(result, empty());
    }

    @Test
    void getRequest_shouldReturnRequest() {
        User user = new User(1L, "User", "user@email.com");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest(1L)).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getRequest(1L, 1L);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getDescription(), equalTo("Need a drill"));
    }

    @Test
    void getRequest_shouldThrowException_whenNotFound() {
        User user = new User(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequest(1L, 1L));
    }
}