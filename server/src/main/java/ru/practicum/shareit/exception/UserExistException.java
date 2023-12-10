package ru.practicum.shareit.exception;

public class UserExistException extends RuntimeException {

    public UserExistException(String s) {
        super(s);
    }
}