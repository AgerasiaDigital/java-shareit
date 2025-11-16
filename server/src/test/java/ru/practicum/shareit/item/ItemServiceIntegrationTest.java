package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(ItemServiceImpl.class)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final TestEntityManager entityManager;

    @Test
    void createItem_shouldCreateItemWithRequestId() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        entityManager.persist(user);

        User requestor = new User();
        requestor.setName("Requestor");
        requestor.setEmail("requestor@email.com");
        entityManager.persist(requestor);

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        entityManager.persist(request);
        entityManager.flush();

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
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        entityManager.persist(user);
        entityManager.flush();

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