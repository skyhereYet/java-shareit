package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;

import java.util.List;

public interface BookingService {

    BookingDtoInfo createBooking(BookingDto bookingDto, int userId);

    BookingDtoInfo updateBooking(int bookingId, int userId, Boolean approved);

    BookingDtoInfo getBooking(int bookingId, int userId);

    List<BookingDtoInfo> getBookingByUserIdAndState(int userId, String state, Pageable pageable);

    List<BookingDtoInfo> getBookingByOwnerAndState(int userId, String state, Pageable pageable);
}