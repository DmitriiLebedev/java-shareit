package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.ValidationException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}