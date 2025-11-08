package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemCreateDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        Item item = ItemMapper.toItem(itemDto, userId);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        if (!existingItem.getOwner().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the item");
        }

        ItemMapper.updateItemFromDto(existingItem, itemDto);

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithBookingsDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        List<Comment> comments = commentRepository.findByItem_Id(itemId);

        ItemWithBookingsDto.BookingShortDto lastBooking = null;
        ItemWithBookingsDto.BookingShortDto nextBooking = null;

        if (item.getOwner().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findByItem_Id(itemId,
                    Sort.by(Sort.Direction.DESC, "start"));

            lastBooking = bookings.stream()
                    .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED))
                    .filter(b -> b.getStart().isBefore(now) || b.getStart().isEqual(now))
                    .findFirst()
                    .map(b -> new ItemWithBookingsDto.BookingShortDto(
                            b.getId(),
                            b.getBooker().getId(),
                            b.getStart(),
                            b.getEnd()
                    ))
                    .orElse(null);

            nextBooking = bookings.stream()
                    .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED))
                    .filter(b -> b.getStart().isAfter(now))
                    .reduce((first, second) -> second) // Берем последнее из отсортированного списка
                    .map(b -> new ItemWithBookingsDto.BookingShortDto(
                            b.getId(),
                            b.getBooker().getId(),
                            b.getStart(),
                            b.getEnd()
                    ))
                    .orElse(null);
        }

        return ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemWithBookingsDto> getItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findAllByOwner(userId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Booking>> bookingsByItem = new HashMap<>();

        for (Long itemId : itemIds) {
            List<Booking> bookings = bookingRepository.findByItem_Id(itemId,
                    Sort.by(Sort.Direction.DESC, "start"));
            bookingsByItem.put(itemId, bookings);
        }

        List<Comment> allComments = commentRepository.findByItem_IdIn(itemIds);
        Map<Long, List<Comment>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), Collections.emptyList());
                    List<Comment> itemComments = commentsByItem.getOrDefault(item.getId(), Collections.emptyList());

                    ItemWithBookingsDto.BookingShortDto lastBooking = itemBookings.stream()
                            .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED))
                            .filter(b -> b.getStart().isBefore(now) || b.getStart().isEqual(now))
                            .findFirst()
                            .map(b -> new ItemWithBookingsDto.BookingShortDto(
                                    b.getId(),
                                    b.getBooker().getId(),
                                    b.getStart(),
                                    b.getEnd()
                            ))
                            .orElse(null);

                    ItemWithBookingsDto.BookingShortDto nextBooking = itemBookings.stream()
                            .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED))
                            .filter(b -> b.getStart().isAfter(now))
                            .reduce((first, second) -> second)
                            .map(b -> new ItemWithBookingsDto.BookingShortDto(
                                    b.getId(),
                                    b.getBooker().getId(),
                                    b.getStart(),
                                    b.getEnd()
                            ))
                            .orElse(null);

                    return ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, itemComments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        LocalDateTime now = LocalDateTime.now();
        boolean hasCompletedBooking = bookingRepository
                .existsCompletedBookingByBookerAndItem(userId, itemId, now);

        if (!hasCompletedBooking) {
            throw new IllegalArgumentException("User has not completed booking for this item");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return ItemMapper.toCommentDto(savedComment);
    }
}