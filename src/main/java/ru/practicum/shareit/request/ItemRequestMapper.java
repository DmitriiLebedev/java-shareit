package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemDto dto, User user) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .requester(user.getId())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static List<ItemRequestDto> toiItemRequestDtoList(List<ItemRequest> itemRequests, List<ItemDto> requestItemDtos) {
        List<ItemRequestDto> list = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            list.add(toItemRequestDto(itemRequest, requestItemDtos));
        }
        return list;
    }


    public static List<ItemDto> toRequestItemDtoList(List<Item> items) {
        List<ItemDto> requestItemDtoList = new ArrayList<>();
        for (Item item : items) {
            requestItemDtoList.add(ItemMapper.toItemDto(item));
        }
        return requestItemDtoList;
    }
}