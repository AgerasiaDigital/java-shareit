package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto_shouldMapItemToDto() {
        Item item = new Item(1L, "Item", "Description", true, 1L, null);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
    }

    @Test
    void toItem_shouldMapCreateDtoToItem() {
        ItemCreateDto dto = new ItemCreateDto("Item", "Description", true, 1L);

        Item item = ItemMapper.toItem(dto, 2L);

        assertEquals("Item", item.getName());
        assertEquals("Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(2L, item.getOwner());
        assertEquals(1L, item.getRequest());
    }

    @Test
    void updateItemFromDto_shouldUpdateOnlyProvidedFields() {
        Item item = new Item(1L, "Old Name", "Old Description", true, 1L, null);
        ItemUpdateDto updateDto = new ItemUpdateDto("New Name", null, null);

        ItemMapper.updateItemFromDto(item, updateDto);

        assertEquals("New Name", item.getName());
        assertEquals("Old Description", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void toItemWithBookingsDto_shouldMapItemWithBookingsAndComments() {
        Item item = new Item(1L, "Item", "Description", true, 1L, null);

        ItemWithBookingsDto dto = ItemMapper.toItemWithBookingsDto(
                item, null, null, Collections.emptyList()
        );

        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertTrue(dto.getComments().isEmpty());
    }
}