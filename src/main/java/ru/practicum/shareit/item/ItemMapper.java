package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getItemRequest() != null) {
            itemDto.setRequestId(item.getItemRequest().getId());
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemRequest);
    }

    public static ItemInfoDto toItemInfoDto(Item item, List<Booking> booking, List<Comment> comments) {
        ItemInfoDto itemInfoDto = new ItemInfoDto();
        itemInfoDto.setId(item.getId());
        itemInfoDto.setName(item.getName());
        itemInfoDto.setDescription(item.getDescription());
        itemInfoDto.setAvailable(item.getAvailable());
        itemInfoDto.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        if (booking.isEmpty()) {
            return itemInfoDto;
        }
        Booking lastBooking = booking.stream()
                .filter(b -> b.getItem().getId() == item.getId())
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .findFirst()
                .orElse(null);
        Booking nextBooking = booking.stream()
                .filter(b -> b.getItem().getId() == item.getId())
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .sorted((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                .findFirst()
                .orElse(null);
        if (lastBooking != null) {
            itemInfoDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemInfoDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }
        return itemInfoDto;
    }
}