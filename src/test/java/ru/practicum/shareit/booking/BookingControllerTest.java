package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Test: Booking controller")
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("BookingController: method - create (should_createBooking_successfully)")
    void should_createBooking_successfully() throws Exception {
        User user = new User(1, "First", "first@email.com");
        User booker = new User(2, "Booker", "booker@email.com");
        Item item = new Item(
                1,
                "Black angle",
                "Don't sleep with him until dawn",
                true,
                user,
                null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                item,
                booker,
                BookingStatus.WAITING
        );
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        BookingDtoInfo bookingDtoInfo = BookingMapper.toBookingDtoInfo(booking);

        when(bookingService.createBooking(any(), anyInt()))
                .thenReturn(bookingDtoInfo);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    @DisplayName("BookingController: method - updateBooking (should_updateBooking_successfully)")
    void should_updateBooking_successfully() throws Exception {
        User user = new User(1, "First", "first@email.com");
        User booker = new User(2, "Booker", "booker@email.com");
        Item item = new Item(
                1,
                "Black angle",
                "Don't sleep with him until dawn",
                true,
                user,
                null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                item,
                booker,
                BookingStatus.APPROVED
        );
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        BookingDtoInfo bookingDtoInfo = BookingMapper.toBookingDtoInfo(booking);

        when(bookingService.updateBooking(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDtoInfo);

        mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    @DisplayName("BookingController: method - getBooking (should_getBooking_successfully)")
    void should_getBooking_successfully() throws Exception {
        User user = new User(1, "First", "first@email.com");
        User booker = new User(2, "Booker", "booker@email.com");
        Item item = new Item(
                1,
                "Black angle",
                "Don't sleep with him until dawn",
                true,
                user,
                null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                item,
                booker,
                BookingStatus.APPROVED
        );
        BookingDtoInfo bookingDtoInfo = BookingMapper.toBookingDtoInfo(booking);

        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenReturn(bookingDtoInfo);

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    @DisplayName("BookingController: method - getBookingByUserIdAndState (should_getBookingByUserIdAndState_successfully)")
    void should_getBookingByUserIdAndState_successfully() throws Exception {
        int from = 0;
        int size = 10;

        when(bookingService.getBookingByUserIdAndState(anyInt(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "WAITING")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.emptyList())));

        verify(bookingService, times(1))
                .getBookingByUserIdAndState(1, "WAITING", PageRequest.of(from / size, size));
    }

    @Test
    @DisplayName("BookingController: method - getBookingByOwner (should_getBookingByOwner_successfully)")
    void should_getBookingByOwner_successfully() throws Exception {
        User user = new User(1, "First", "first@email.com");
        User booker = new User(2, "Booker", "booker@email.com");
        Item item = new Item(
                1,
                "Black angle",
                "Don't sleep with him until dawn",
                true,
                user,
                null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                item,
                booker,
                BookingStatus.APPROVED
        );
        BookingDtoInfo bookingDtoInfo = BookingMapper.toBookingDtoInfo(booking);

        when(bookingService.getBookingByOwnerAndState(anyInt(), any(), any()))
                .thenReturn(List.of(bookingDtoInfo));

        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", "1")
                                .param("state", "ALL")
                                .param("from", "1")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoInfo))));
    }
}