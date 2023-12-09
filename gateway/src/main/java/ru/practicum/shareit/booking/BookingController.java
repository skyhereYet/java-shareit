package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingPropertiesException;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
												@Validated({Create.class}) @RequestBody BookingDto bookingDto) {
		log.info("GATEWAY: Creating booking {}, userId={}", bookingDto, userId);
		return bookingClient.createBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> ubdateBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
												@PathVariable long bookingId,
												@RequestParam Boolean approved) {
		log.info("GATEWAY: Updating booking ID={}, userId={}", bookingId, userId);
		return bookingClient.updateBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
											 @PathVariable Long bookingId) {
		log.info("GATEWAY: Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
												@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												@PositiveOrZero @RequestParam(name = "from",
														defaultValue = "0") Integer from,
												@Positive @RequestParam(name = "size",
														defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new BookingPropertiesException("Unknown state: " + stateParam));
		log.info("GATEWAY: Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
													  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
													@PositiveOrZero @RequestParam(name = "from",
															defaultValue = "0") Integer from,
													@Positive @RequestParam(name = "size",
															defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new BookingPropertiesException("Unknown state: " + stateParam));
		log.info("GATEWAY: Get booking by owner ID={} with state {}, from={}, size={}", userId, state, from, size);
		return bookingClient.getBookingByOwner(userId, state, from, size);
	}
}