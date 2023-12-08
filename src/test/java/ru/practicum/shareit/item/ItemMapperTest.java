package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("Test: User Mapper")
class ItemMapperTest {


    @Test
    void toItemInfoDto() {
        User owner = new User(1,"toItemInfoDto", "toItemInfoDto@email.ru");
        User requestor = new User(2,"requestor", "requestor@email.ru");
        ItemRequest itemRequest = new ItemRequest(1, "Black angle", requestor, LocalDateTime.now());
        Item item = new Item(1, "Black angle", "Very grey", true, owner, itemRequest);
        Booking booking1 = new Booking(
                1,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusSeconds(5),
                item,
                requestor,
                BookingStatus.APPROVED);
        Booking booking2 = new Booking(
                2,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(5),
                item,
                requestor,
                BookingStatus.APPROVED);
        Comment comment1 = new Comment(1, "Comment1", item, requestor, LocalDateTime.now());
        Comment comment2 = new Comment(2, "Comment2", item, requestor, LocalDateTime.now().minusHours(1));
        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item, List.of(booking1, booking2), List.of(comment1, comment2));

        assertEquals(itemInfoDto.getId(), item.getId());
        assertEquals(itemInfoDto.getDescription(), item.getDescription());
        assertEquals(itemInfoDto.getName(), item.getName());
        assertEquals(itemInfoDto.getAvailable(), item.getAvailable());
        assertEquals(itemInfoDto.getComments().size(), 2);

    }
}