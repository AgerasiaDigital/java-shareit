package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @Valid @RequestBody ItemRequestDto requestDto) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Creating request from user {}: {}", userId, requestDto);
        return itemRequestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Get requests for user {}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Get all requests from={}, size={}", from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long requestId) {
        if (userId == null) {
            throw new RuntimeException("User ID header is required");
        }
        log.info("Get request {} by user {}", requestId, userId);
        return itemRequestClient.getRequest(userId, requestId);
    }
}