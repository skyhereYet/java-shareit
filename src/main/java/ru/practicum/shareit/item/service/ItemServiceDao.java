package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidCommentRequestException;
import ru.practicum.shareit.exception.ItemExistException;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceDao implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = false)
    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID = "  + userId));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, owner, null)));
    }

    @Transactional(readOnly = false)
    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID = "  + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemExistException("Item not exist in the repository, ID = "  + itemId));
        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public Item getItemByIdOrThrow(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemExistException("Item not exist in the repository, ID = "  + id)
        );
    }

    @Override
    public List<ItemInfoDto> getItemsInfoDtoByUserId(int userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Booking> bookingList = bookingRepository.findAllByItemOwnerStartDesc(userId);
        List<Comment> comments = new ArrayList<>();//commentRepository.findCommentByAuthorOrderByCreated()
        return items.stream()
                .sorted(Comparator.comparingInt(Item ::getId))
                .map(i -> ItemMapper.toItemInfoDto(i, bookingList, comments))
                .collect(Collectors.toList());
    }

    @Override
    public ItemInfoDto getItemInfoDtoByIdOrThrow(int itemId, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemExistException("Item not exist in the repository, ID = "  + itemId));
        List<Comment> comments = commentRepository.findCommentByItemOrderByCreated(item);
        List<Booking> bookingList = new ArrayList<>();
        if (item.getOwner().getId() == userId) {
           bookingList = bookingRepository.findAllByItem_IdAndStatusOrderByStart(itemId, BookingStatus.APPROVED);
        }
        return ItemMapper.toItemInfoDto(item, bookingList, comments);
    }

    @Override
    public List<ItemDto> getItemsBySubstring(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        text = "%" + text + "%";
        return itemRepository.findItemsByRequest(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public CommentDto createComment(int itemId, int userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemExistException("Item not exist in the repository, ID = "  + itemId));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID = "  + userId));
        List<Booking> bookingList = bookingRepository
                .findAllByItemAndBookerAndEndBeforeOrderByStart(userId, itemId, LocalDateTime.now());
        boolean bookerExist = false;
        if (!bookingList.isEmpty()) {
            for (Booking booker : bookingList) {
                if (booker.getEnd().isBefore(LocalDateTime.now())) {
                    bookerExist = true;
                    break;
                }
            }
        } else {
            throw new InvalidCommentRequestException("User not booking item");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        Comment toReturn = commentRepository.save(comment);
        return CommentDto.toCommentDto(toReturn);
    }
}