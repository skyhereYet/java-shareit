package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.exception.StateCheckException;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED;

    public static BookingStatus toBookingStatus(State state) {
        for (BookingStatus bookingStatus : BookingStatus.values()) {
            if (state.toString().equals(bookingStatus.toString())) {
                return bookingStatus;
            }
        }
        throw new StateCheckException("Unknown status: " + state);
    }
}