package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingRequest bookingRequest);

    BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookerBookings(Long userId, String state, int from, int size);

    List<BookingDto> getAllBookerItemsBooking(Long ownerId, String state, int from, int size);
}