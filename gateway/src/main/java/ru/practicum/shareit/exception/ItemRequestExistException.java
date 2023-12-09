package ru.practicum.shareit.exception;

public class ItemRequestExistException extends RuntimeException {
    public ItemRequestExistException(String message) {
        super(message);
    }
}
