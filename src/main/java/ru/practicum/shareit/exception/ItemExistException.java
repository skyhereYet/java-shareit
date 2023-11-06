package ru.practicum.shareit.exception;

public class ItemExistException extends RuntimeException {

    public ItemExistException(String message) {
        super(message);
    }
}
