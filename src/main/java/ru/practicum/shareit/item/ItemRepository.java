package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> itemsByOwner = new HashMap<>();
    private Long currentId = 1L;

    public Item save(Item item) {
        item.setId(currentId++);
        items.put(item.getId(), item);

        itemsByOwner.computeIfAbsent(item.getOwner(), k -> new ArrayList<>())
                .add(item.getId());

        return item;
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAllByOwner(Long ownerId) {
        return itemsByOwner.getOrDefault(ownerId, Collections.emptyList())
                .stream()
                .map(items::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(searchText) ||
                                item.getDescription().toLowerCase().contains(searchText)
                )
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        Item item = items.remove(id);
        if (item != null) {
            List<Long> ownerItems = itemsByOwner.get(item.getOwner());
            if (ownerItems != null) {
                ownerItems.remove(id);
            }
        }
    }
}