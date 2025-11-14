package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void createItem_shouldCreateItemWithRequestId() {
        User user = new User(null, "Test User", "test@email.com");
        user = userRepository.save(user);

        User requestor = new User(null, "Requestor", "requestor@email.com");
        requestor = userRepository.save(requestor);

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);

        ItemCreateDto createDto = new ItemCreateDto(
                "Drill",
                "Powerful drill",
                true,
                request.getId()
        );

        ItemDto result = itemService.createItem(user.getId(), createDto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo("Drill"));
        assertThat(result.getDescription(), equalTo("Powerful drill"));
        assertThat(result.getAvailable(), is(true));
        assertThat(result.getRequestId(), equalTo(request.getId()));
    }

    @Test
    void createItem_shouldCreateItemWithoutRequestId() {
        User user = new User(null, "Test User", "test@email.com");
        user = userRepository.save(user);

        ItemCreateDto createDto = new ItemCreateDto(
                "Drill",
                "Powerful drill",
                true,
                null
        );

        ItemDto result = itemService.createItem(user.getId(), createDto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo("Drill"));
        assertThat(result.getDescription(), equalTo("Powerful drill"));
        assertThat(result.getAvailable(), is(true));
        assertThat(result.getRequestId(), nullValue());
    }
}