package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingForItem;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookingModel {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingForItem lastBooking;
    BookingForItem nextBooking;
    List<CommentDto> comments;
}
