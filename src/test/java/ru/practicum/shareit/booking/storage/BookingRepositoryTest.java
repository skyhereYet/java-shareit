package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Test: BookingRepository")
class BookingRepositoryTest {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final ItemRepository itemRepository;

    User user;
    User user2;
    Item item;
    Booking booking1;
    Booking booking2;
    Booking booking3;

    PageRequest pageRequest = PageRequest.of(0, 20);
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().plusHours(2);

    //@BeforeEach
    void beforeEach() {
        //itemRepository.deleteAll();
        //bookingRepository.deleteAll();
        //userRepository.deleteAll();
        Random random = new Random();
        String generatedEmail = "generate" + (random.nextInt(100) * random.nextInt(100)) + "@email.com";

        user = userRepository.save(new User(1, "First user", generatedEmail));
        item = itemRepository.save(new Item(1, "Black angle", "So good", true,
                user, null));

        generatedEmail = "generate" + (random.nextInt(100) * random.nextInt(100)) + "@email.com";
        user2 = userRepository.save(new User(2, "Second user", generatedEmail));
        booking1 = new Booking();
        booking1.setStart(start);
        booking1.setEnd(end);
        booking1.setItem(item);
        booking1.setBooker(user);
        booking1.setId(1);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1 = bookingRepository.save(booking1);
        booking2 = new Booking();
        booking2.setStart(start.plusDays(1));
        booking2.setEnd(end.plusDays(3));
        booking2.setItem(item);
        booking2.setBooker(user);
        booking2.setId(2);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2 = bookingRepository.save(booking2);
        booking3 = new Booking();
        booking3.setStart(start.plusDays(1));
        booking3.setEnd(end.plusDays(3));
        booking3.setItem(item);
        booking3.setBooker(user2);
        booking3.setId(3);
        booking3.setStatus(BookingStatus.REJECTED);
        booking3 = bookingRepository.save(booking3);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByBooker (should_findAllByBooker_successfully)")
    @Order(1)
    @Rollback(value = false)
    void should_findAllByBooker_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByBooker(bookingList.get(0).getBooker().getId(), pageRequest);
        assertEquals(list.size(), 2);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByBookerAndStartGreaterThan (should_findAllByBookerAndStartGreaterThan_successfully)")
    @Order(2)
    @Rollback(value = false)
    void should_findAllByBookerAndStartGreaterThan_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByBookerAndStartGreaterThan(
                bookingList.get(0).getBooker().getId(), start.plusHours(2), pageRequest);
        assertEquals(list.size(), 1);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByBookerAndStartBefore (should_findAllByBookerAndStartBefore_successfully)")
    @Order(3)
    @Rollback(value = false)
    void should_findAllByBookerAndStartBefore_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByBookerAndStartBefore(
                bookingList.get(0).getBooker().getId(), start.plusDays(1), pageRequest);
        assertEquals(list.size(), 1);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByBookerAndStatus (should_findAllByBookerAndStatus_successfully)")
    @Order(4)
    @Rollback(value = false)
    void should_findAllByBookerAndStatus_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByBookerAndStatus(
                bookingList.get(0).getBooker().getId(),
                BookingStatus.APPROVED, pageRequest);
        assertEquals(list.size(), 2);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByItemOwnerAndStatus (should_findAllByItemOwnerAndStatus_successfully)")
    @Order(4)
    @Rollback(value = false)
    void should_findAllByItemOwnerAndStatus_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByItemOwnerAndStatus(
                bookingList.get(0).getItem().getOwner().getId(),
                BookingStatus.APPROVED, pageRequest);
        assertEquals(list.size(), 2);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByItemAndBookerAndEndBeforeOrderByStart (should_findAllByItemAndBookerAndEndBeforeOrderByStart_successfully)")
    @Order(5)
    @Rollback(value = false)
    void should_findAllByItemAndBookerAndEndBeforeOrderByStart_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByItemAndBookerAndEndBeforeOrderByStart(
                bookingList.get(0).getBooker().getId(),
                bookingList.get(0).getItem().getId(),
                start.plusDays(10)
                );
        assertEquals(list.size(), 2);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByItemOwnerStartDescPageable (should_findAllByItemOwnerStartDescPageable_successfully)")
    @Order(6)
    @Rollback(value = false)
    void should_findAllByItemOwnerStartDescPageable_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByItemOwnerStartDescPageable(
                bookingList.get(0).getItem().getOwner().getId(),
                pageRequest
        );
        assertEquals(list.size(), 3);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByItemOwnerStartDesc (should_findAllByItemOwnerStartDesc_successfully)")
    @Order(7)
    @Rollback(value = false)
    void should_findAllByItemOwnerStartDesc_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByItemOwnerStartDesc(
                bookingList.get(0).getItem().getOwner().getId());
        assertEquals(list.size(), 3);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByItemOwnerAndStartGreaterThanOrderByStart (findAllByItemOwnerAndStartGreaterThanOrderByStart)")
    @Order(8)
    @Rollback(value = false)
    void should_findAllByItemOwnerAndStartGreaterThanOrderByStart_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByItemOwnerAndStartGreaterThanOrderByStart(
                bookingList.get(0).getItem().getOwner().getId(),
                start.plusHours(5),
                pageRequest);
        assertEquals(list.size(), 2);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByItemOwnerAndStartBefore (should_findAllByItemOwnerAndStartBefore_successfully)")
    @Order(9)
    @Rollback(value = false)
    void should_findAllByItemOwnerAndStartBefore_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByItemOwnerAndStartBefore(
                bookingList.get(0).getItem().getOwner().getId(),
                start.plusHours(5),
                pageRequest);
        assertEquals(list.size(), 1);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByItem_IdAndStatusOrderByStart (should_findAllByItem_IdAndStatusOrderByStart_successfully)")
    @Order(10)
    @Rollback(value = false)
    void should_findAllByItem_IdAndStatusOrderByStart_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByItem_IdAndStatusOrderByStart(
                bookingList.get(0).getItem().getId(),
                BookingStatus.REJECTED
                );
        assertEquals(list.size(), 1);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByBookerAndCurrent (should_findAllByBookerAndCurrent_successfully)")
    @Order(11)
    @Rollback(value = false)
    void should_findAllByBookerAndCurrent_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByBookerAndCurrent(
                bookingList.get(0).getBooker().getId(),
                LocalDateTime.now(),
                pageRequest
                );
        assertEquals(list.size(), 1);
    }

    @Test
    @DisplayName("BookingRepository: method - findAllByItemOwnerAndCurrent (should_findAllByItemOwnerAndCurrent_successfully)")
    @Order(12)
    @Rollback(value = false)
    void should_findAllByItemOwnerAndCurrent_successfully() {
        beforeEach();
        List<Booking> bookingList = bookingRepository.findAll();
        List<Booking> list = bookingRepository.findAllByBookerAndCurrent(
                bookingList.get(0).getItem().getOwner().getId(),
                LocalDateTime.now(),
                pageRequest
                );
        assertEquals(list.size(), 1);
    }
}