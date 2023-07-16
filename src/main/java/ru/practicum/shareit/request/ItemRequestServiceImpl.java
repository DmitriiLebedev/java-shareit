package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {

    UserService userService;

    ItemRequestRepository repository;

    ItemRepository itemRepository;

    UserRepository userRepository;


    @Override
    public ItemRequestDto create(long userId, ItemDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (userRepository.findAll().size() < userId)
            throw new NotFoundException("Can't find user id:" + userId);
        else {
            User user = userService.getUserOptional(userId);
            ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
            List<ItemDto> requestItemDtoList = new ArrayList<>();
            return ItemRequestMapper.toItemRequestDto(repository.save(itemRequest), requestItemDtoList);
        }
    }

    @Override
    public List<ItemRequestDto> get(long userId, int from, int size) {
        User user = userService.getUserOptional(userId);
        List<ItemRequest> itemRequest = repository.findAllByRequesterIsNotOrderByCreatedDesc(user.getId(), PageRequest.of(from, size));
        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequest.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));
        List<ItemDto> itemsDto = ItemRequestMapper.toRequestItemDtoList(items);
        return ItemRequestMapper.toiItemRequestDtoList(itemRequest, itemsDto);
    }

    @Override
    public List<ItemRequestDto> getByUserId(long userId) {
        User user = userService.getUserOptional(userId);
        List<ItemRequest> itemRequest = repository.findAllByRequesterOrderByCreatedDesc(user.getId());
        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequest.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));
        List<ItemDto> itemsDto = ItemRequestMapper.toRequestItemDtoList(items);
        return ItemRequestMapper.toiItemRequestDtoList(itemRequest, itemsDto);
    }

    @Override
    public ItemRequestDto getRequestId(long requestId, long userId) {
        userService.getUserOptional(userId);
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));
        List<ItemDto> itemsDto = ItemRequestMapper.toRequestItemDtoList(items);
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsDto);
    }
}