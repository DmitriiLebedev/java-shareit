package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemStorage itemStorage;
    final UserStorage userStorage;
    final ItemMapper itemMapper;
    final UserMapper userMapper;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        UserDto owner = userMapper.toUserDto(userStorage.findUserById(ownerId));
        itemDto.setOwner(owner);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        return itemMapper.toItemDto(itemStorage.findItemById(itemId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        Item item = itemStorage.findItemById(itemId);
        itemDto.setId(itemId);
        itemDto.setOwner(userMapper.toUserDto(userStorage.findUserById(ownerId)));
        if (itemDto.getOwner() != null && !itemDto.getOwner().getId().equals(item.getOwner().getId())) {
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
        return itemMapper.toItemDto(itemStorage.update(item));
    }

    @Override
    public List<ItemDto> findAllItemsByOwner(Long ownerId) {
        userStorage.findUserById(ownerId);
        List<Item> items = itemStorage.findAllItemsByOwner(ownerId);
        return itemDtoToList(items);
    }

    @Override
    public List<ItemDto> search(String subString) {
        List<Item> items = itemStorage.search(subString);
        return itemDtoToList(items);
    }

    private List<ItemDto> itemDtoToList(List<Item> items) {
        List<ItemDto> dto = new ArrayList<>();
        for (Item item : items) {
            dto.add(itemMapper.toItemDto(item));
        }
        return dto;
    }
}