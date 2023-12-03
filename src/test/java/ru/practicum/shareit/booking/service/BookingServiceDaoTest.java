package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingExistException;
import ru.practicum.shareit.exception.BookingPropertiesException;
import ru.practicum.shareit.exception.StateCheckException;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceDao;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Test: Booking service")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceDaoTest {

    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private final BookingService bookingService;
    @Autowired
    private final ItemServiceDao itemService;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final BookingRepository bookingRepository;

    void beforeEach() {
        Random random = new Random();
        String generatedEmail = "generate" + (random.nextInt(100) * random.nextInt(100)) + "@email.com";
        User user1 = userRepository.save(new User(1, "First user", generatedEmail));
        generatedEmail = "generate" + (random.nextInt(100) * random.nextInt(100)) + "@email.com";
        User user2 = userRepository.save(new User(2, "Second user", generatedEmail));
        Item item = itemRepository.save(new Item(1, "Black angle", "So good", true,
                user1, null));
    }

    @Test
    @Order(value = 1)
    @DisplayName("ItemRequestServiceDao: method - createBooking (should_createBooking_successfully)")
    @Rollback(false)
    void should_createBooking_successfully() {
        beforeEach();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(LocalDateTime.now());
        int userId = userRepository.findAll().get(1).getId();
        int itemId = itemRepository.findAll().get(0).getId();
        bookingDto.setItemId(itemId);

        BookingDtoInfo bookingDtoInfo = bookingService.createBooking(bookingDto, userId);

        TypedQuery<Booking> query = entityManager.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking booker = query.setParameter("id", bookingDtoInfo.getId()).getSingleResult();

        assertThat(booker.getId(), equalTo(bookingDtoInfo.getId()));
        assertThat(booker.getStart(), equalTo(bookingDtoInfo.getStart()));
        assertThat(booker.getEnd(), equalTo(bookingDtoInfo.getEnd()));
        assertThat(booker.getItem(), equalTo(bookingDtoInfo.getItem()));
        assertThat(booker.getBooker(), equalTo(bookingDtoInfo.getBooker()));
    }

    @Test
    @Order(value = 2)
    @DisplayName("ItemRequestServiceDao: method - updateBooking (should_updateBooking_successfully)")
    @Rollback(value = true)
    void should_updateBooking_successfully() {
        beforeEach();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(LocalDateTime.now());
        int userId = userRepository.findAll().get(1).getId();
        int itemId = itemRepository.findAll().get(0).getId();
        bookingDto.setItemId(itemId);
        BookingDtoInfo bookingDtoInfo = bookingService.createBooking(bookingDto, userId);

        int userIdNew = userRepository.findAll().get(0).getId();
        int bookerId = bookingRepository.findAll().get(0).getId();
        bookingDtoInfo = bookingService.updateBooking(bookerId, userIdNew, true);
        TypedQuery<Booking> query = entityManager.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking booker = query.setParameter("id", bookingDtoInfo.getId()).getSingleResult();

        assertThat(booker.getStatus(), equalTo(bookingDtoInfo.getStatus()));
        Assertions.assertThrows(UserExistException.class, () -> {
            bookingService.updateBooking(bookerId, userId, true);
        });

        Assertions.assertThrows(BookingPropertiesException.class, () -> {
            bookingService.updateBooking(bookerId, userIdNew, false);
        });
        Assertions.assertThrows(UserExistException.class, () -> {
            bookingService.updateBooking(bookerId, 1111, true);
        });
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(false);
        itemService.updateItem(itemDto, userIdNew, itemId);
        Assertions.assertThrows(BookingPropertiesException.class, () -> {
            bookingService.updateBooking(bookerId, userIdNew, false);
        });
        itemDto.setAvailable(true);
        itemService.updateItem(itemDto, userIdNew, itemId);
    }

    @Test
    @Order(value = 2)
    @DisplayName("ItemRequestServiceDao: method - getBooking (should_getBooking_successfully)")
    @Rollback(value = true)
    void should_getBooking_successfully() {
        beforeEach();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(LocalDateTime.now());
        int bookerId = userRepository.findAll().get(1).getId();
        int itemId = itemRepository.findAll().get(0).getId();
        bookingDto.setItemId(itemId);
        BookingDtoInfo bookingDtoInfo = bookingService.createBooking(bookingDto, bookerId);

        TypedQuery<Booking> query = entityManager.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking booker = query.setParameter("id", bookingDtoInfo.getId()).getSingleResult();
        BookingDtoInfo bookingDtoInfoDao = bookingService.getBooking(bookingDtoInfo.getId(), bookerId);
        assertThat(booker.getId(), equalTo(bookingDtoInfoDao.getId()));
        assertThat(booker.getStart(), equalTo(bookingDtoInfoDao.getStart()));
        assertThat(booker.getEnd(), equalTo(bookingDtoInfoDao.getEnd()));
        assertThat(booker.getItem(), equalTo(bookingDtoInfoDao.getItem()));
        assertThat(booker.getBooker(), equalTo(bookingDtoInfoDao.getBooker()));
        Assertions.assertThrows(BookingExistException.class, () -> {
            bookingService.getBooking(1000, bookerId);
        });
        Assertions.assertThrows(UserExistException.class, () -> {
            bookingService.getBooking(bookingDtoInfo.getId(), 10000);
        });
    }

    @Test
    @Order(value = 3)
    @DisplayName("ItemRequestServiceDao: method - getBookingByUserIdAndState (should_getBookingByUserIdAndState_successfully)")
    @Rollback(value = true)
    void should_getBookingByUserIdAndState_successfully() {
        beforeEach();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(LocalDateTime.now());
        int bookerId = userRepository.findAll().get(1).getId();
        int itemId = itemRepository.findAll().get(0).getId();
        bookingDto.setItemId(itemId);
        BookingDtoInfo bookingDtoInfo = bookingService.createBooking(bookingDto, bookerId);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setEnd(LocalDateTime.now().plusSeconds(5));
        bookingDto2.setStatus(BookingStatus.APPROVED);
        bookingDto2.setStart(LocalDateTime.now());
        bookingDto2.setItemId(itemId);
        BookingDtoInfo bookingDtoInfo2 = bookingService.createBooking(bookingDto2, bookerId);

        BookingDto bookingDto3 = new BookingDto();
        bookingDto3.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto3.setStatus(BookingStatus.REJECTED);
        bookingDto3.setStart(LocalDateTime.now().plusHours(1));
        bookingDto3.setItemId(itemId);
        BookingDtoInfo bookingDtoInfo3 = bookingService.createBooking(bookingDto3, bookerId);

        int from = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        int ownerId = itemRepository.findAll().get(0).getOwner().getId();

        List<BookingDtoInfo> bookingDtoList = bookingService.getBookingByUserIdAndState(bookerId, "CURRENT", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(3));
        bookingDtoList = bookingService.getBookingByUserIdAndState(bookerId, "ALL", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(4));

        bookingDtoList = bookingService.getBookingByUserIdAndState(bookerId, "FUTURE", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(1));
        bookingDtoList = bookingService.getBookingByUserIdAndState(bookerId, "PAST", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(0));
        bookingDtoList = bookingService.getBookingByUserIdAndState(bookerId, "REJECTED", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(1));
        bookingDtoList = bookingService.getBookingByUserIdAndState(bookerId, "WAITING", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(2));

        Assertions.assertThrows(StateCheckException.class, () -> {
            bookingService.getBookingByUserIdAndState(bookerId, "CURNT", pageRequest);
        });
        Assertions.assertThrows(UserExistException.class, () -> {
            bookingService.getBookingByUserIdAndState(1000, "FUTURE", pageRequest);
        });
        bookingDtoList = bookingService.getBookingByUserIdAndState(ownerId, "ALL", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(0));
    }

    @Test
    @Order(value = 3)
    @DisplayName("ItemRequestServiceDao: method - getBookingByOwnerAndState (should_getBookingByOwnerAndState_successfully)")
    @Rollback(value = true)
    void should_getBookingByOwnerAndState_successfully() {
        beforeEach();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(LocalDateTime.now());
        int bookerId = userRepository.findAll().get(1).getId();
        int itemId = itemRepository.findAll().get(0).getId();
        bookingDto.setItemId(itemId);
        BookingDtoInfo bookingDtoInfo = bookingService.createBooking(bookingDto, bookerId);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setEnd(LocalDateTime.now().plusSeconds(5));
        bookingDto2.setStatus(BookingStatus.APPROVED);
        bookingDto2.setStart(LocalDateTime.now());
        bookingDto2.setItemId(itemId);
        BookingDtoInfo bookingDtoInfo2 = bookingService.createBooking(bookingDto2, bookerId);

        BookingDto bookingDto3 = new BookingDto();
        bookingDto3.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto3.setStatus(BookingStatus.REJECTED);
        bookingDto3.setStart(LocalDateTime.now().plusHours(1));
        bookingDto3.setItemId(itemId);
        BookingDtoInfo bookingDtoInfo3 = bookingService.createBooking(bookingDto3, bookerId);

        int from = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        int ownerId = itemRepository.findAll().get(0).getOwner().getId();

        List<BookingDtoInfo> bookingDtoList = bookingService.getBookingByOwnerAndState(ownerId, "CURRENT", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(3));
        bookingDtoList = bookingService.getBookingByOwnerAndState(ownerId, "ALL", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(4));

        bookingDtoList = bookingService.getBookingByOwnerAndState(ownerId, "FUTURE", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(1));
        bookingDtoList = bookingService.getBookingByOwnerAndState(ownerId, "PAST", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(0));
        bookingDtoList = bookingService.getBookingByOwnerAndState(ownerId, "REJECTED", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(1));
        bookingDtoList = bookingService.getBookingByOwnerAndState(ownerId, "WAITING", pageRequest);
        assertThat(bookingDtoList.size(), equalTo(2));

        Assertions.assertThrows(StateCheckException.class, () -> {
            bookingService.getBookingByOwnerAndState(ownerId, "CURNT", pageRequest);
        });
        Assertions.assertThrows(UserExistException.class, () -> {
            bookingService.getBookingByOwnerAndState(1000, "PAST", pageRequest);
        });
    }
}