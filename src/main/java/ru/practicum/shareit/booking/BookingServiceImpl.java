package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, BookingRequest bookingRequest) {
        Long id = bookingRequest.getItemId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingRequest.getStart();
        LocalDateTime end = bookingRequest.getEnd();
        checkUser(userId);
        if (!itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Can't find item id:" + id))
                .getAvailable()) {
            throw new UnavailableException("Booking is unavailable");
        }
        if (start.isBefore(now) || end.isBefore(now) || start.equals(end) || start.isAfter(end)) {
            throw new UnavailableException("Booking is unavailable");
        }
        if (userId.equals(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Can't find item id:" + id))
                .getOwnerId())) {
            throw new NotFoundException("Wrong owner");
        }
        Booking booking = BookingMapper.toBooking(bookingRequest);
        booking.setItem(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Can't find item id:" + id)));
        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Can't find user id:" + userId)));
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        checkUser(userId);
        Booking booking = getBookingOptional(bookingId);
        if (booking.getItem().getOwnerId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Booking is unavailable");
        }
    }

    @Transactional
    @Override
    public BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingOptional(bookingId);
        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Wrong owner");
        }
        if (BookingStatus.APPROVED == booking.getStatus()) {
            throw new UnavailableException("Booking is already confirmed");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookerBookings(Long userId, String state, int from, int size) {
        if (from < 0 || size < 1) throw new ValidationException("Incorrect parameters");
        checkUser(userId);
        BookingState newState = BookingState.parseState(state);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (newState) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBookerIdCurrentBookings(userId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                BookingStatus status = BookingStatus.valueOf(String.valueOf(state));
                return bookingRepository.findALLByBookerIdAndStatusOrderByStartDesc(userId, status, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingDto> getAllBookerItemsBooking(Long ownerId, String state, int from, int size) {
        if (from < 0 || size < 1) throw new ValidationException("Incorrect parameters");
        checkUser(ownerId);
        BookingState newState = BookingState.parseState(state);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (newState) {
            case ALL:
                return bookingRepository.findAllByOwnerIdOrderByStartDesc(ownerId, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByOwnerIdCurrentBookings(ownerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                BookingStatus status = BookingStatus.valueOf(String.valueOf(state));
                return bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(ownerId, status, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    private void checkUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) throw new NotFoundException("Can't find user id:" + userId);
    }

    private Booking getBookingOptional(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Can't find booking id:" + bookingId));
    }

}
