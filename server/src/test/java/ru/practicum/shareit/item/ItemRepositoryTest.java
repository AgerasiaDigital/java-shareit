package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@example.com");
        em.persist(owner);
        em.flush();
    }

    @Test
    void findAllByOwner_shouldReturnOwnerItems() {
        Item item1 = new Item(null, "Item1", "Desc1", true, owner.getId(), null);
        Item item2 = new Item(null, "Item2", "Desc2", true, owner.getId(), null);
        em.persist(item1);
        em.persist(item2);
        em.flush();

        List<Item> items = itemRepository.findAllByOwner(owner.getId());

        assertEquals(2, items.size());
    }

    @Test
    void search_shouldReturnItemsByNameOrDescription() {
        Item item1 = new Item(null, "Drill", "Power drill", true, owner.getId(), null);
        Item item2 = new Item(null, "Hammer", "Heavy hammer", true, owner.getId(), null);
        Item item3 = new Item(null, "Saw", "Hand saw", true, owner.getId(), null);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.flush();

        List<Item> results = itemRepository.search("drill");

        assertEquals(1, results.size());
        assertEquals("Drill", results.get(0).getName());
    }

    @Test
    void search_shouldReturnEmptyList_whenNoMatches() {
        Item item = new Item(null, "Drill", "Power drill", true, owner.getId(), null);
        em.persist(item);
        em.flush();

        List<Item> results = itemRepository.search("hammer");

        assertTrue(results.isEmpty());
    }

    @Test
    void search_shouldNotReturnUnavailableItems() {
        Item item1 = new Item(null, "Drill", "Power drill", true, owner.getId(), null);
        Item item2 = new Item(null, "Drill Pro", "Professional drill", false, owner.getId(), null);
        em.persist(item1);
        em.persist(item2);
        em.flush();

        List<Item> results = itemRepository.search("drill");

        assertEquals(1, results.size());
        assertEquals("Drill", results.get(0).getName());
    }

    @Test
    void findByRequest_shouldReturnItemsForRequest() {
        Item item1 = new Item(null, "Item1", "Desc1", true, owner.getId(), 1L);
        Item item2 = new Item(null, "Item2", "Desc2", true, owner.getId(), 1L);
        Item item3 = new Item(null, "Item3", "Desc3", true, owner.getId(), 2L);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.flush();

        List<Item> results = itemRepository.findByRequest(1L);

        assertEquals(2, results.size());
    }
}