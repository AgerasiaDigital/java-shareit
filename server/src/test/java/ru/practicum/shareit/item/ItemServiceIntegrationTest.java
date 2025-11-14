package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserRepository userRepository;

    @Test
    void createItem_shouldCreateItemWithRequestId() {
        User user = new User(null, "Test User", "test@email.com");
        user = userRepository.save(user);

        ItemCreateDto createDto = new ItemCreateDto(
                "Drill",
                "Powerful drill",
                true,
                1L
        );

        ItemDto result = itemService.createItem(user.getId(), createDto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo("Drill"));
        assertThat(result.getDescription(), equalTo("Powerful drill"));
        assertThat(result.getAvailable(), is(true));
        assertThat(result.getRequestId(), equalTo(1L));
    }
}