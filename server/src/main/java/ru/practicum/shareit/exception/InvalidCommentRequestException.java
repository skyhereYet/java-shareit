package ru.practicum.shareit.exception;

public class InvalidCommentRequestException extends RuntimeException {

    public InvalidCommentRequestException(String message) {
        super(message);
    }
}