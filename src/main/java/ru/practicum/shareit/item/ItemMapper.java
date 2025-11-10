package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest());
        return dto;
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(Item item,
                                                            ItemWithBookingsDto.BookingShortDto lastBooking,
                                                            ItemWithBookingsDto.BookingShortDto nextBooking,
                                                            List<Comment> comments) {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);

        if (comments != null) {
            dto.setComments(comments.stream()
                    .map(ItemMapper::toCommentDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setComments(new ArrayList<>());
        }

        return dto;
    }

    public static Item toItem(ItemCreateDto itemDto, Long ownerId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(ownerId);
        item.setRequest(itemDto.getRequestId());
        return item;
    }

    public static void updateItemFromDto(Item item, ItemUpdateDto updateDto) {
        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            item.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            item.setDescription(updateDto.getDescription());
        }
        if (updateDto.getAvailable() != null) {
            item.setAvailable(updateDto.getAvailable());
        }
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}