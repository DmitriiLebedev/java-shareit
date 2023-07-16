package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemDto;

import java.util.List;


public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemDto itemRequestDto);

    List<ItemRequestDto> get(long userId, int from, int size);

    List<ItemRequestDto> getByUserId(long userId);

    ItemRequestDto getRequestId(long requestId, long userId);
}