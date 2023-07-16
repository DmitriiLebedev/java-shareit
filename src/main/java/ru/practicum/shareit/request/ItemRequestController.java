package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {

    final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody ItemDto itemRequestDto) {
        return requestService.create(userId, itemRequestDto);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(value = "size", defaultValue = "20") @Positive int size,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.get(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestId(@PathVariable @Positive long requestId,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequestId(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getByUserId(userId);
    }
}
