package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDto.BookerDto(booking.getBooker().getId()),
                new BookingDto.ItemDto(
                        booking.getItem().getId(),
                        booking.getItem().getName()
                )
        );
    }
}