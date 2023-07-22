package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRequest;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    ItemBookingModel findItemById(Long userId, Long itemId);

    List<ItemBookingModel> findAllItemsByOwner(Long ownerId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto createComment(Long userId, Long itemId, CommentRequest commentRequest);

}
