package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest()
        );
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
        dto.setComments(comments.stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public static Item toItem(ItemCreateDto itemDto, Long ownerId) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId,
                itemDto.getRequestId()
        );
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
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}