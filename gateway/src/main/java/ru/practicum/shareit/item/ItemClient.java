package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> createItem(long userId, ItemCreateDto itemDto) {
        return post(API_PREFIX, userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemUpdateDto itemDto) {
        return patch(API_PREFIX + "/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get(API_PREFIX + "/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwner(long userId) {
        return get(API_PREFIX, userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get(API_PREFIX + "/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, commentDto);
    }
}