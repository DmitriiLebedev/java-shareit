package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemStorageImpl implements ItemStorage {

    final Map<Long, Item> items = new HashMap<>();
    Long id = 0L;

    private Long increaseId() {
        return ++id;
    }

    @Override
    public Item create(Item item) {
        item.setId(increaseId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> findAllItemsByOwner(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item findItemById(Long itemId) {
        checkId(itemId);
        return items.get(itemId);
    }

    @Override
    public Item update(Item item) {
        checkId(item.getId());
        items.replace(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> search(String subString) {
        if (subString.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(subString.toLowerCase())
                        || item.getName().toLowerCase().contains(subString.toLowerCase())) && item.getAvailable())
                .collect(Collectors.toList());
    }

    private void checkId(Long id) {
        if (!items.containsKey(id)) {
            throw new NoSuchElementException("Can't find item id:" + id);
        }
    }
}
