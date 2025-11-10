package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> createBooking(long userId, BookingCreateDto bookingDto) {
        return post(API_PREFIX, userId, bookingDto);
    }

    public ResponseEntity<Object> updateBookingStatus(long userId, long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch(API_PREFIX + "/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get(API_PREFIX + "?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get(API_PREFIX + "/owner?state={state}", userId, parameters);
    }
}