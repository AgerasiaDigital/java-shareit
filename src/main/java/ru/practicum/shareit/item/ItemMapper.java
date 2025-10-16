package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

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
}