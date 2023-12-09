package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserExistException(final UserExistException e) {
        log.warn("Error caught: 404, " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemExistException(final ItemExistException e) {
        log.warn("Error caught: 404, " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExistException(final EmailExistException e) {
        log.warn("Error caught: 409, " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingPropertiesException(final BookingPropertiesException e) {
        log.warn("Error caught: 400, " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingExistException(final BookingExistException e) {
        log.warn("Error caught: 404, " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStateCheckException(final StateCheckException e) {
        log.warn("Error caught: 400, " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidCommentRequestException(final InvalidCommentRequestException e) {
        log.warn("Error caught: 400, " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemRequestExistException(final ItemRequestExistException e) {
        log.warn("Error caught: 404, " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace().toString());
    }
}