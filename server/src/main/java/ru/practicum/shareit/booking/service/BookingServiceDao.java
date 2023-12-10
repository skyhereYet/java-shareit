package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingExistException;
import ru.practicum.shareit.exception.BookingPropertiesException;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceDao implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = false)
    @Fetch(FetchMode.JOIN)
    public BookingDtoInfo createBooking(BookingDto bookingDto, int userId) {
        Item item = itemService.getItemByIdOrThrow(bookingDto.getItemId());
        User user = UserMapper.toUser(userService.getUserByIdOrThrow(userId));
        if (item.getOwner().getId() == userId) {
            throw new BookingExistException("The item belongs to the user who sent the request");
        }
        if (!item.getAvailable()) {
            throw new BookingPropertiesException("The item unvailable");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        }
        return BookingMapper.toBookingDtoInfo(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = false)
    public BookingDtoInfo updateBooking(int bookingId, int userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingExistException("Booking with ID - " + bookingId + " not found"));
        userService.getUserByIdOrThrow(userId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new UserExistException("Error: owner ID does not match user ID");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingPropertiesException("Booking status is " + booking.getStatus());
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoInfo(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoInfo getBooking(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingExistException("Booking with ID - " + bookingId + " not found"));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new UserExistException("Can be done either by the author of the booking or by the owner of the item");
        }
        return BookingMapper.toBookingDtoInfo(booking);
    }

    @Override
    public List<BookingDtoInfo> getBookingByUserIdAndState(int userId, String stateString, Pageable pageable) {
        userService.getUserByIdOrThrow(userId);
        State state = State.checkState(stateString);
        if (state.equals(State.ALL)) {
            return bookingRepository.findAllByBooker(userId, pageable)
                    .stream()
                    .map(BookingMapper::toBookingDtoInfo)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.FUTURE)) {
            return bookingRepository.findAllByBookerAndStartGreaterThan(userId, LocalDateTime.now(), pageable)
                    .stream()
                    .map(BookingMapper::toBookingDtoInfo)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.PAST)) {
            return bookingRepository.findAllByBookerAndStartBefore(userId, LocalDateTime.now(), pageable)
                    .stream()
                    .map(BookingMapper::toBookingDtoInfo)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.CURRENT)) {
            return bookingRepository.findAllByBookerAndCurrent(userId, LocalDateTime.now(), pageable)
                    .stream()
                    .map(BookingMapper::toBookingDtoInfo)
                    .collect(Collectors.toList());
        }
        return bookingRepository.findAllByBookerAndStatus(userId, BookingStatus.toBookingStatus(state), pageable)
                .stream()
                .map(BookingMapper::toBookingDtoInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoInfo> getBookingByOwnerAndState(int userId, String stateString, Pageable pageable) {
        userService.getUserByIdOrThrow(userId);
        State state = State.checkState(stateString);
        if (state.equals(State.ALL)) {
            return bookingRepository.findAllByItemOwnerStartDescPageable(userId, pageable)
                    .stream()
                    .map(BookingMapper::toBookingDtoInfo)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.FUTURE)) {
            return bookingRepository.findAllByItemOwnerAndStartGreaterThanOrderByStart(userId, LocalDateTime.now(), pageable)
                    .stream()
                    .map(BookingMapper::toBookingDtoInfo)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.PAST)) {
            return bookingRepository.findAllByItemOwnerAndStartBefore(userId, LocalDateTime.now(), pageable)
                    .stream()
                    .map(BookingMapper::toBookingDtoInfo)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.CURRENT)) {
            return bookingRepository.findAllByItemOwnerAndCurrent(userId, LocalDateTime.now(), pageable)
                    .stream()
                    .map(BookingMapper::toBookingDtoInfo)
                    .collect(Collectors.toList());
        }
        return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.toBookingStatus(state), pageable).stream()
                .map(BookingMapper::toBookingDtoInfo)
                .collect(Collectors.toList());
    }
}