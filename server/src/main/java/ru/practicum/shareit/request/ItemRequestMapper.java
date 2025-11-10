package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        if (items != null) {
            dto.setItems(items.stream()
                    .map(item -> new ItemRequestDto.ItemDto(
                            item.getId(),
                            item.getName(),
                            item.getOwner()
                    ))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto dto, Long requestorId) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestorId);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}