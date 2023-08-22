package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid BookingRequest bookingRequest) {
        return bookingService.addBooking(userId, bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(value = "from", defaultValue = "0") int from,
                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingService.getAllBookerBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserItemsBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingService.getAllBookerItemsBooking(ownerId, state, from, size);
    }
}
