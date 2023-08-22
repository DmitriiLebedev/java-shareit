package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemServiceImpl itemService;

    @GetMapping
    public List<ItemBookingModel> findAllItemsByOwner(
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "20") @Positive int size,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllItemsByOwner(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemBookingModel findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("itemId") Long itemId) {
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam("text") String subString,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemService.search(subString, from, size);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.update(itemDto, itemId, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemId") Long itemId,
                                 @Valid @RequestBody CommentRequest commentRequest) {
        return itemService.createComment(userId, itemId, commentRequest);
    }
}
