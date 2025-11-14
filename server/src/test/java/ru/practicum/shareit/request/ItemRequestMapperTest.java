package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDto_shouldMapCorrectly() {
        User user = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, created);

        Item item1 = new Item(1L, "Drill 1", "Description 1", true, 2L, 1L);
        Item item2 = new Item(2L, "Drill 2", "Description 2", true, 3L, 1L);

        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(request, List.of(item1, item2));

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getDescription(), equalTo("Need a drill"));
        assertThat(result.getCreated(), equalTo(created));
        assertThat(result.getItems(), hasSize(2));
        assertThat(result.getItems().get(0).getId(), equalTo(1L));
        assertThat(result.getItems().get(0).getName(), equalTo("Drill 1"));
        assertThat(result.getItems().get(0).getOwnerId(), equalTo(2L));
    }

    @Test
    void toItemRequestDto_shouldMapWithEmptyItems() {
        User user = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, created);

        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(request, List.of());

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getItems(), empty());
    }
}