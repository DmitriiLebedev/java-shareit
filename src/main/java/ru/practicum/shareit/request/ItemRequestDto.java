package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items = new ArrayList<>();
}