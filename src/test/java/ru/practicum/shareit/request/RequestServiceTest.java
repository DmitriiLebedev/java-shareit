package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceTest {

    final ItemRequestService service;
    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final ItemRequestRepository requestRepository;
    User user;
    User user1;
    Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John")
                .email("john.doe@mail.com")
                .build();
        user1 = User.builder()
                .id(2L)
                .name("Jane")
                .email("jane.doe@mail.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("thing")
                .available(true)
                .ownerId(1L)
                .requestId(null)
                .build();
    }

    @Test
    void shouldCreateRequest() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        ItemDto itemRequestDto = ItemDto.builder()
                .description("thing")
                .build();
        ItemRequestDto requestDto = service.create(1, itemRequestDto);
        assertEquals(requestDto.getDescription(), itemRequestDto.getDescription());

    }

    @Test
    void shouldGetRequestById() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        ItemRequest itemRequest = ItemRequest.builder()
                .description("thing")
                .requester(1L)
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);
        ItemDto itemRequestDto = ItemDto.builder()
                .description("thing")
                .build();
        service.create(1, itemRequestDto);
        ItemRequestDto requestDto = service.getRequestId(1, 1);
        assertEquals(requestDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void shouldThrowExceptionCreatingRequestWhenUserNotFound() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        ItemDto dto = ItemDto.builder()
                .description("thing")
                .build();
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.create(99, dto));
        assertEquals("Can't find user id:99", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionGettingRequestWhenUserNotFound() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        ItemRequest itemRequest = ItemRequest.builder()
                .description("thing")
                .requester(1L)
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);
        ItemDto itemRequestDto = ItemDto.builder()
                .description("thing")
                .build();
        service.create(1, itemRequestDto);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.get(99, 1, 5));
        assertEquals("Can't find user id:99", exception.getMessage());
    }

    @Test
    void shouldGetRequest() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        ItemRequest itemRequest = ItemRequest.builder()
                .description("thing")
                .requester(1L)
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);
        ItemDto itemRequestDto = ItemDto.builder()
                .description("thing")
                .build();
        List<ItemRequestDto> itemRequestResponseDto = service.getByUserId(1);
        assertEquals(itemRequestResponseDto.size(), 1);
    }

    @Test
    void shouldThrowExceptionGettingRequestByWrongUserId() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        ItemRequest itemRequest = ItemRequest.builder()
                .description("thing")
                .requester(1L)
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);
        ItemDto itemRequestDto = ItemDto.builder()
                .description("thing")
                .build();
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getByUserId(99));
        assertEquals("Can't find user id:99", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionGettingRequestByWrongId() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        ItemRequest itemRequest = ItemRequest.builder()
                .description("thing")
                .requester(1L)
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);
        ItemDto itemRequestDto = ItemDto.builder()
                .description("thing")
                .build();
        service.create(1, itemRequestDto);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getRequestId(1, 99));
        assertEquals("Can't find user id:99", exception.getMessage());
    }
}