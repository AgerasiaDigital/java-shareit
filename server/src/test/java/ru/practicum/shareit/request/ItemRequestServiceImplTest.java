package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user;
    private ItemRequest request;
    private ItemRequestCreateDto createDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "test@example.com");
        request = new ItemRequest(1L, "Need a drill", 1L, LocalDateTime.now());
        createDto = new ItemRequestCreateDto("Need a drill");
    }

    @Test
    void createRequest_shouldCreateRequest_whenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = requestService.createRequest(1L, createDto);

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_shouldThrowNotFoundException_whenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.createRequest(1L, createDto));
        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getUserRequests_shouldReturnRequests_whenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestor(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = requestService.getUserRequests(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getUserRequests_shouldThrowNotFoundException_whenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getUserRequests(1L));
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorNot(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = requestService.getAllRequests(1L);

        assertNotNull(result);
        verify(requestRepository).findByRequestorNot(eq(1L), any(Sort.class));
    }

    @Test
    void getRequestById_shouldReturnRequest_whenRequestExists() {
        Item item = new Item(1L, "Drill", "Power drill", true, 2L, 1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAll()).thenReturn(List.of(item));

        ItemRequestDto result = requestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertFalse(result.getItems().isEmpty());
    }

    @Test
    void getRequestById_shouldThrowNotFoundException_whenRequestNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(1L, 1L));
    }
}