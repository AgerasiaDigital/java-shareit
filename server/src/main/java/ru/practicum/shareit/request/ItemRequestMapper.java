package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        List<ItemRequestDto.ItemDto> itemDtos = items.stream()
                .map(item -> {
                    ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
                    itemDto.setId(item.getId());
                    itemDto.setName(item.getName());
                    itemDto.setOwnerId(item.getOwner());
                    return itemDto;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}