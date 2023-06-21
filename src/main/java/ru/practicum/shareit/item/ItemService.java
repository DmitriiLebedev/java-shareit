package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    ItemDto findItemById(Long itemId);

    List<ItemDto> findAllItemsByOwner(Long ownerId);

    List<ItemDto> search(String subString);
}
