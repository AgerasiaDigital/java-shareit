package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;

    @Test
    void createRequest_shouldCreateRequest() {
        User user = new User(null, "Test User", "test@email.com");
        user = userRepository.save(user);

        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");

        ItemRequestDto result = itemRequestService.createRequest(user.getId(), createDto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getDescription(), equalTo("Need a drill"));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), empty());
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() {
        User user = new User(null, "Test User", "test@email.com");
        user = userRepository.save(user);

        ItemRequestCreateDto createDto1 = new ItemRequestCreateDto("Need a drill");
        ItemRequestCreateDto createDto2 = new ItemRequestCreateDto("Need a saw");

        itemRequestService.createRequest(user.getId(), createDto1);
        itemRequestService.createRequest(user.getId(), createDto2);

        List<ItemRequestDto> results = itemRequestService.getUserRequests(user.getId());

        assertThat(results, hasSize(2));
        assertThat(results.get(0).getDescription(), equalTo("Need a saw"));
        assertThat(results.get(1).getDescription(), equalTo("Need a drill"));
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        User user1 = new User(null, "User 1", "user1@email.com");
        user1 = userRepository.save(user1);

        User user2 = new User(null, "User 2", "user2@email.com");
        user2 = userRepository.save(user2);

        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");
        itemRequestService.createRequest(user1.getId(), createDto);

        List<ItemRequestDto> results = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getDescription(), equalTo("Need a drill"));
    }

    @Test
    void getRequest_shouldReturnRequestWithItems() {
        User user = new User(null, "Test User", "test@email.com");
        user = userRepository.save(user);

        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");
        ItemRequestDto created = itemRequestService.createRequest(user.getId(), createDto);

        ItemRequestDto result = itemRequestService.getRequest(user.getId(), created.getId());

        assertThat(result.getId(), equalTo(created.getId()));
        assertThat(result.getDescription(), equalTo("Need a drill"));
        assertThat(result.getItems(), notNullValue());
    }
}