package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ItemRequestServiceImpl.class, ItemServiceImpl.class})
class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final TestEntityManager entityManager;

    @Test
    void createRequest_shouldCreateRequest() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        entityManager.persist(user);
        entityManager.flush();

        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");

        ItemRequestDto result = itemRequestService.createRequest(user.getId(), createDto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getDescription(), equalTo("Need a drill"));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), empty());
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        entityManager.persist(user);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Need a drill");
        request1.setRequestor(user);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        entityManager.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Need a saw");
        request2.setRequestor(user);
        request2.setCreated(LocalDateTime.now());
        entityManager.persist(request2);
        entityManager.flush();

        List<ItemRequestDto> results = itemRequestService.getUserRequests(user.getId());

        assertThat(results, hasSize(2));
        assertThat(results.get(0).getDescription(), equalTo("Need a saw"));
        assertThat(results.get(1).getDescription(), equalTo("Need a drill"));
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@email.com");
        entityManager.persist(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@email.com");
        entityManager.persist(user2);

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setRequestor(user1);
        request.setCreated(LocalDateTime.now());
        entityManager.persist(request);
        entityManager.flush();

        List<ItemRequestDto> results = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getDescription(), equalTo("Need a drill"));
    }

    @Test
    void getRequest_shouldReturnRequestWithItems() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        entityManager.persist(user);

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        entityManager.persist(request);
        entityManager.flush();

        ItemRequestDto result = itemRequestService.getRequest(user.getId(), request.getId());

        assertThat(result.getId(), equalTo(request.getId()));
        assertThat(result.getDescription(), equalTo("Need a drill"));
        assertThat(result.getItems(), notNullValue());
    }
}