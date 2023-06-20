package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item findItemById(Long itemId);

    List<Item> findAll(Long ownerId);

    List<Item> search(String subString);
}
