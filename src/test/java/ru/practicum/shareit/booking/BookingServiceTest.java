package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingServiceImpl bookingService;
    Booking booking;
    Booking futureBooking;
    Booking pastBooking;
    User user;
    User user1;
    Item item;
    List<Booking> bookings;

    @BeforeEach
    void setUp() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime pastTime = currentTime.minusDays(1);
        LocalDateTime futureTime = currentTime.plusDays(1);
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
        lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        item = new Item(1L, "item", "desc", true, 2L, 1L);
        booking = new Booking(1L, currentTime, currentTime.plusHours(1), item, user, BookingStatus.APPROVED);
        futureBooking = new Booking(2L, futureTime, futureTime.plusHours(1), item, user, BookingStatus.APPROVED);
        pastBooking = new Booking(3L, pastTime, pastTime.plusHours(1), item, user, BookingStatus.APPROVED);
    }

    @Test
    void shouldThrowExceptionWhileAddingBookingWithWrongUserId() {
        LocalDateTime newStart = LocalDateTime.now();
        LocalDateTime newEnd = newStart.minusDays(1);
        BookingRequest newBookingRequest = new BookingRequest(item.getId(), newStart, newEnd);
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(0L, newBookingRequest));
        assertEquals("Can't find user id:" + 0L, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhileAddingBookingWhenItemIsUnavailable() {
        LocalDateTime newStart = LocalDateTime.now();
        LocalDateTime newEnd = newStart.minusDays(1);
        BookingRequest newBookingRequest = new BookingRequest(item.getId(), newStart, newEnd);
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        UnavailableException ex = assertThrows(UnavailableException.class,
                () -> bookingService.addBooking(user.getId(), newBookingRequest));
        assertEquals("Booking is unavailable", ex.getMessage());
    }

    @Test
    void shouldFindBookingById() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto expectedBooking = BookingMapper.toBookingDto(booking);
        BookingDto actualBooking = bookingService.getBookingById(user.getId(), booking.getId());
        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void shouldThrowExceptionWhileFindingBookingByWrongId() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(user.getId(), 0L));
        assertEquals("Can't find booking id:" + 0L, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhileFindingBookingByWrongUserId() {
        User user2 = User.builder()
                .id(3L)
                .name("Bob")
                .email("bob.doe@mail.com")
                .build();
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(3L, booking.getId()));
        assertEquals("Booking is unavailable", ex.getMessage());
    }

    @Test
    void shouldFindListOfALLBookings() {
        bookings = List.of(booking, futureBooking, pastBooking);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        List<BookingDto> bookings = bookingService.getAllBookerBookings
                (1L, "ALL", 0, 10);
        assertEquals(3, bookings.size());
    }

    @Test
    void shouldFindListOfREJECTEDBookings() {
        List<BookingDto> bookingsR = bookingService.getAllBookerBookings
                (1L, "REJECTED", 0, 10);
        assertEquals(0, bookingsR.size());
    }

    @Test
    void shouldFindListOfPASTBookings() {
        List<BookingDto> bookingsP = bookingService.getAllBookerBookings
                (1L, "PAST", 0, 10);
        assertEquals(0, bookingsP.size());
    }

    @Test
    void shouldFindListOfCURRENTBookings() {
        List<BookingDto> bookingsC = bookingService.getAllBookerBookings
                (1L, "CURRENT", 0, 10);
        assertEquals(0, bookingsC.size());
    }

    @Test
    void shouldFindListOfWAITINGBookings() {
        List<BookingDto> bookingsW = bookingService.getAllBookerBookings
                (1L, "WAITING", 0, 10);
        assertEquals(0, bookingsW.size());
    }

    @Test
    void shouldFindListOfFUTUREBookings() {
        List<BookingDto> bookingsF = bookingService.getAllBookerBookings
                (1L, "FUTURE", 0, 10);
        assertEquals(0, bookingsF.size());
    }

    @Test
    void shouldThrowExceptionWhileGettingBookingsIfStateIsWrong() {
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getAllBookerBookings(1L, "WRONG_STATE", 0, 10));
    }

    @Test
    void shouldFindListOfAllBookingsWithStateALL() {
        bookings = List.of(booking, futureBooking, pastBooking);
        when(bookingRepository.findAllByOwnerIdOrderByStartDesc(anyLong(), any())).thenReturn(bookings);
        List<BookingDto> bookings = bookingService.getAllBookerItemsBooking
                (2L, "ALL", 0, 10);
        assertEquals(3, bookings.size());
    }

    @Test
    void shouldFindListOfAllBookingsWithStateREJECTED() {
        List<BookingDto> bookingsR = bookingService.getAllBookerItemsBooking
                (2L, "REJECTED", 0, 10);
        assertEquals(0, bookingsR.size());
    }

    @Test
    void shouldFindListOfAllBookingsWithStateWAITING() {
        List<BookingDto> bookingsW = bookingService.getAllBookerItemsBooking
                (2L, "WAITING", 0, 10);
        assertEquals(0, bookingsW.size());
    }

    @Test
    void shouldFindListOfAllBookingsWithStateCURRENT() {
        List<BookingDto> bookingsC = bookingService.getAllBookerItemsBooking
                (2L, "CURRENT", 0, 10);
        assertEquals(0, bookingsC.size());
    }

    @Test
    void shouldFindListOfAllBookingsWithStateFUTURE() {
        List<BookingDto> bookingsF = bookingService.getAllBookerItemsBooking
                (2L, "FUTURE", 0, 10);
        assertEquals(0, bookingsF.size());
    }

    @Test
    void shouldFindListOfAllBookingsWithStatePAST() {
        List<BookingDto> bookingsP = bookingService.getAllBookerItemsBooking
                (2L, "PAST", 0, 10);
        assertEquals(0, bookingsP.size());
    }

    @Test
    void shouldThrowExceptionWhileAddingBookingWhenDateIsIncorrect() {
        LocalDateTime newStart = LocalDateTime.now();
        LocalDateTime newEnd = newStart.minusDays(1);
        BookingRequest newBookingRequest = new BookingRequest(item.getId(), newStart, newEnd);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        UnavailableException ex = assertThrows(UnavailableException.class,
                () -> bookingService.addBooking(user.getId(), newBookingRequest));
        assertEquals("Booking is unavailable", ex.getMessage());
    }

    @Test
    void shouldReturnBookingWithApprovedStatus() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingDto actualBooking = bookingService.updateBookingStatus(user1.getId(), booking.getId(), true);
        assertEquals(booking.getStatus(), actualBooking.getStatus());
    }

    @Test
    void shouldThrowExceptionWhileStatusIsAlreadyApproved() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        UnavailableException ex = assertThrows(UnavailableException.class,
                () -> bookingService.updateBookingStatus(user1.getId(), booking.getId(), true));
        assertEquals("Booking is already confirmed", ex.getMessage());
    }
}