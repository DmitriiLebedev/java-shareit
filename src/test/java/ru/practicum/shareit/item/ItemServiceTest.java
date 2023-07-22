package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceTest {
    final ItemRepository itemRepository;

    final BookingRepository bookingRepository;

    final UserRepository userRepository;

    final ItemService itemService;

    User user;

    Item item;

    ItemDto itemDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();
        userRepository.save(user);
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("desc")
                .available(true)
                .build();
        item = ItemMapper.toItem(itemService.create(user.getId(), itemDto));
        item.setOwnerId(1L);
        itemRepository.save(item);
        itemDto = ItemMapper.toItemDto(item);
    }

    @Test
    @Transactional
    void shouldCreateItem() {
        assertNotNull(item);
        assertEquals(user.getId(), item.getOwnerId());
    }

    @Test
    void shouldFindItem() {
        itemRepository.save(item);
        ItemBookingModel bookingModel = itemService.findItemById(1L, 1L);
        assertEquals(1L, bookingModel.getId());
    }

    @Test
    void shouldFindAllItemsByOwner() {
        itemRepository.save(item);
        List<ItemBookingModel> list = itemService.findAllItemsByOwner(user.getId(), 0, 20);
        assertEquals(1, list.size());
    }

    @Test
    void shouldPostComment() {
        bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED));
        CommentRequest request = new CommentRequest("text");
        itemService.createComment(user.getId(), item.getId(), request);
        CommentDto commentDto = itemService.createComment(user.getId(), item.getId(), request);
        assertEquals(commentDto.getText(), request.getText());
    }

    @Test
    @Transactional
    void shouldThrowExceptionCreatingItemIfUserIdIsWrong() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.create(99L, itemDto));
        assertEquals("Can't find user id:99", exception.getMessage());
    }

    @Test
    @Transactional
    void shouldUpdate() {
        itemRepository.save(item);
        ItemDto dto1 = ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("updated")
                .available(true)
                .build();
        ItemDto item1 = itemService.update(dto1, 1L, 1L);
        assertNotNull(item1);
        assertEquals(item1.getName(), dto1.getName());
        assertEquals(item1.getDescription(), dto1.getDescription());
    }

    @Test
    void shouldSearchAndFind2ItemsWithSimilarName() {
        Item item2 = Item.builder()
                .name("item1")
                .description("desc")
                .available(true)
                .ownerId(1L)
                .build();
        itemRepository.save(item2);
        List<ItemDto> itemList = itemService.search("item", 0, 20);
        assertEquals(itemList.size(), 2);
    }

    @Test
    void shouldNotSearchWithEmptyText() {
        List<ItemDto> itemList = itemService.search("", 0, 20);
        assertEquals(itemList.size(), 0);
    }

    @Test
    void shouldThrowExceptionWhenUserCommentingBeforeEndOfBooking() {
        bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, user, BookingStatus.APPROVED));
        CommentRequest request = new CommentRequest("text");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createComment(user.getId(), item.getId(), request));
        assertEquals("Can't find bookings", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUserOfCommentNotFound() {
        bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED));
        CommentRequest request = new CommentRequest("text");
        itemService.createComment(user.getId(), item.getId(), request);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createComment(99L, item.getId(), request));
        assertEquals("Can't find bookings", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfItemOfCommentNotFound() {
        bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED));
        CommentRequest request = new CommentRequest("text");
        itemService.createComment(user.getId(), item.getId(), request);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createComment(user.getId(), 99L, request));
        assertEquals("Can't find bookings", exception.getMessage());
    }

    @Test
    @Transactional
    void shouldThrowExceptionAndNotUpdateIfUserNotFound() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemDto, 1L, 99L));
        assertEquals("Can't find user id:99", exception.getMessage());
    }

    @Test
    @Transactional
    void shouldThrowExceptionAndNotUpdateIfItemNotFound() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemDto, 99L, 1L));
        assertEquals("Can't find item id:99", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionFindingItemByWrongUserId() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.findItemById(99L, 1L));
        assertEquals("Can't find user id:99", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionFindingItemByWrongItemId() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.findItemById(1L, 99L));
        assertEquals("Can't find item id:99", exception.getMessage());
    }
}