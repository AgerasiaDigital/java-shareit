package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> createRequest(long userId, ItemRequestCreateDto requestDto) {
        return post(API_PREFIX, userId, requestDto);
    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        return get(API_PREFIX, userId);
    }

    public ResponseEntity<Object> getAllRequests(long userId) {
        return get(API_PREFIX + "/all", userId);
    }

    public ResponseEntity<Object> getRequestById(long userId, long requestId) {
        return get(API_PREFIX + "/" + requestId, userId);
    }
}