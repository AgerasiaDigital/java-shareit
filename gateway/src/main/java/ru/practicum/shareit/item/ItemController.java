package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @Valid @RequestBody ItemCreateDto itemDto) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Creating item by user {}: {}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemUpdateDto itemDto) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Updating item {} by user {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long itemId) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Get item {} by user {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Get items by owner {}", userId);
        return itemClient.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Search items with text: {}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Adding comment to item {} by user {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}