package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingForItem;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final UserService userService;
    final ItemRepository itemRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        UserDto owner = userService.findUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(owner.getId());
        item.setRequestId(item.getRequestId());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemBookingModel findItemById(Long userId, Long itemId) {
        Item item = getItemOptional(userId, itemId);
        ItemBookingModel itemWithDates = ItemMapper.toItemWithDatesDto(item);
        LocalDateTime currentTime = LocalDateTime.now();
        if (item.getOwnerId().equals(userId)) {
            BookingForItem lastBooking = bookingRepository.findLastBookingForItem(itemId, currentTime, BookingStatus.APPROVED)
                    .stream().findFirst().orElse(null);
            BookingForItem nextBooking = bookingRepository.findNextBookingForItem(itemId, currentTime, BookingStatus.APPROVED)
                    .stream().findFirst().orElse(null);
            itemWithDates.setLastBooking(lastBooking);
            itemWithDates.setNextBooking(nextBooking);
        }
        List<CommentDto> comments = commentRepository.findAllByItem(itemId);
        itemWithDates.setComments(comments);
        return itemWithDates;
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        Item item = getItemOptional(ownerId, itemId);
        itemDto.setId(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Item " + itemId + " can't be changed by this user");
        }
        if (itemDto.getName() != null && !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(item.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemBookingModel> findAllItemsByOwner(Long ownerId, Integer from, Integer size) {
        userService.getUserOptional(ownerId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId, page);
        return items.stream()
                .map(item -> findItemById(ownerId, item.getId()))
                .sorted(Comparator.comparing(ItemBookingModel::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> items = itemRepository.findItemsByTextIgnoreCase(text.toLowerCase(), page);
        return ItemMapper.toItemDtoList(items);
    }

    public CommentDto createComment(Long userId, Long itemId, CommentRequest commentRequest) {
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemId(userId, itemId)
                .stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) throw new ValidationException("Can't find bookings");
        Comment comment = CommentMapper.toComment(commentRequest);
        comment.setItem(getItemOptional(userId, itemId));
        comment.setAuthor(userService.getUserOptional(userId));
        Comment newComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(newComment);
    }

    private Item getItemOptional(Long userId, Long itemId) {
        userService.getUserOptional(userId);
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Can't find item id:" + itemId));
    }
}