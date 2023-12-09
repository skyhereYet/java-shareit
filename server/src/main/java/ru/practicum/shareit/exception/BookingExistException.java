package ru.practicum.shareit.exception;

public class BookingExistException extends RuntimeException {

    public BookingExistException(String message) {
        super(message);
    }
}