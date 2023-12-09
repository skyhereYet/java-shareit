package ru.practicum.shareit.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    @Autowired
    private final BookingService bookingService;
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoInfo createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestBody BookingDto bookingDto) {
        log.info("POST request. Create booking. User ID - " + userId + ", BookingDto = " + bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDtoInfo updateBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @PathVariable int bookingId,
                                        @RequestParam Boolean approved) {
        log.info("PATCH request. Approved - " + approved + ", userId - " + userId + ", booking ID " + bookingId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingDtoInfo getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @PathVariable int bookingId) {
        log.info("GET request. user ID - " + userId + ", booking ID " + bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoInfo> getBookingByUserIdAndState(@RequestHeader("X-Sharer-User-Id") int userId,
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("GET request. user ID - " + userId);
        return bookingService.getBookingByUserIdAndState(userId, state, PageRequest.of(from / size, size));
    }

    @GetMapping(value = "/owner")
    public List<BookingDtoInfo> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("GET request. user ID - " + userId + " state - " + state);
        return bookingService.getBookingByOwnerAndState(userId, state, PageRequest.of(from / size, size));
    }
}