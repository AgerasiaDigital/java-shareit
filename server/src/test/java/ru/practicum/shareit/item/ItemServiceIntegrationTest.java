package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@test.com"));
    }

    @Test
    void createItem_shouldSaveItemToDatabase() {
        ItemCreateDto itemDto = new ItemCreateDto("Test Item", "Description", true, null);

        ItemDto created = itemService.createItem(owner.getId(), itemDto);

        assertNotNull(created.getId());
        assertTrue(itemRepository.existsById(created.getId()));
        assertEquals("Test Item", created.getName());
    }

    @Test
    void updateItem_shouldUpdateItemInDatabase() {
        ItemCreateDto itemDto = new ItemCreateDto("Original", "Original Desc", true, null);
        ItemDto created = itemService.createItem(owner.getId(), itemDto);

        ItemUpdateDto updateDto = new ItemUpdateDto("Updated", null, false);
        ItemDto updated = itemService.updateItem(owner.getId(), created.getId(), updateDto);

        assertEquals("Updated", updated.getName());
        assertEquals("Original Desc", updated.getDescription());
        assertEquals(false, updated.getAvailable());
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        itemService.createItem(owner.getId(),
                new ItemCreateDto("Drill", "Power drill", true, null));
        itemService.createItem(owner.getId(),
                new ItemCreateDto("Hammer", "Heavy hammer", true, null));

        List<ItemDto> results = itemService.searchItems("drill");

        assertEquals(1, results.size());
        assertEquals("Drill", results.get(0).getName());
    }

    @Test
    void getItemsByOwner_shouldReturnOwnerItems() {
        itemService.createItem(owner.getId(),
                new ItemCreateDto("Item 1", "Description 1", true, null));
        itemService.createItem(owner.getId(),
                new ItemCreateDto("Item 2", "Description 2", true, null));

        List<ItemWithBookingsDto> items = itemService.getItemsByOwner(owner.getId());

        assertEquals(2, items.size());
    }
}