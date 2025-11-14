package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemMapperTest {

    @Test
    void toItemDto_shouldMapCorrectly() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, 2L);

        ItemDto result = ItemMapper.toItemDto(item);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Drill"));
        assertThat(result.getDescription(), equalTo("Powerful drill"));
        assertThat(result.getAvailable(), is(true));
        assertThat(result.getRequestId(), equalTo(2L));
    }

    @Test
    void toItemWithBookingsDto_shouldMapCorrectly() {
        Item item = new Item(1L, "Drill", "Powerful drill", true, 1L, null);
        User author = new User(2L, "Author", "author@email.com");
        Comment comment = new Comment(1L, "Great!", item, author, LocalDateTime.now());

        ItemWithBookingsDto.BookingShortDto lastBooking =
                new ItemWithBookingsDto.BookingShortDto(1L, 2L, LocalDateTime.now().minusDays(1), LocalDateTime.now());
        ItemWithBookingsDto.BookingShortDto nextBooking =
                new ItemWithBookingsDto.BookingShortDto(2L, 3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        ItemWithBookingsDto result = ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, List.of(comment));

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Drill"));
        assertThat(result.getLastBooking(), notNullValue());
        assertThat(result.getNextBooking(), notNullValue());
        assertThat(result.getComments(), hasSize(1));
    }

    @Test
    void toItem_shouldMapCorrectly() {
        ItemCreateDto dto = new ItemCreateDto("Drill", "Powerful drill", true, 2L);

        Item result = ItemMapper.toItem(dto, 1L);

        assertThat(result.getName(), equalTo("Drill"));
        assertThat(result.getDescription(), equalTo("Powerful drill"));
        assertThat(result.getAvailable(), is(true));
        assertThat(result.getOwner(), equalTo(1L));
        assertThat(result.getRequest(), equalTo(2L));
    }

    @Test
    void updateItemFromDto_shouldUpdateFields() {
        Item item = new Item(1L, "Old Name", "Old Description", true, 1L, null);
        ItemUpdateDto updateDto = new ItemUpdateDto("New Name", "New Description", false);

        ItemMapper.updateItemFromDto(item, updateDto);

        assertThat(item.getName(), equalTo("New Name"));
        assertThat(item.getDescription(), equalTo("New Description"));
        assertThat(item.getAvailable(), is(false));
    }

    @Test
    void updateItemFromDto_shouldNotUpdateNullFields() {
        Item item = new Item(1L, "Old Name", "Old Description", true, 1L, null);
        ItemUpdateDto updateDto = new ItemUpdateDto(null, null, null);

        ItemMapper.updateItemFromDto(item, updateDto);

        assertThat(item.getName(), equalTo("Old Name"));
        assertThat(item.getDescription(), equalTo("Old Description"));
        assertThat(item.getAvailable(), is(true));
    }

    @Test
    void toCommentDto_shouldMapCorrectly() {
        User author = new User(1L, "Author", "author@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(1L, "Great item!", item, author, created);

        CommentDto result = ItemMapper.toCommentDto(comment);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getText(), equalTo("Great item!"));
        assertThat(result.getAuthorName(), equalTo("Author"));
        assertThat(result.getCreated(), equalTo(created));
    }

    @Test
    void toComment_shouldMapCorrectly() {
        User author = new User(1L, "Author", "author@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, 2L, null);
        CommentDto dto = new CommentDto();
        dto.setText("Great item!");

        Comment result = ItemMapper.toComment(dto, item, author);

        assertThat(result.getText(), equalTo("Great item!"));
        assertThat(result.getItem(), equalTo(item));
        assertThat(result.getAuthor(), equalTo(author));
        assertThat(result.getCreated(), notNullValue());
    }
}