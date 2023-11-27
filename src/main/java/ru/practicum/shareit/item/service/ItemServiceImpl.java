package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemExistException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private final ItemStorage itemStorage;
    private final UserServiceImpl userService;
    private final ItemMapper itemMapper;

    private int id = 1;

    public ItemServiceImpl(ItemStorage itemStorage, UserServiceImpl userService, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        User user = UserMapper.toUser(userService.getUserByIdOrThrow(userId));
        Item item = ItemMapper.toItem(itemDto, user, null);
        item.setId(id++);
        Item itemToReturn = itemStorage.createItem(item);
        log.info("Succesfully item created and saved: " + itemToReturn);
        return ItemMapper.toItemDto(itemToReturn);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        User user = UserMapper.toUser(userService.getUserByIdOrThrow(userId));
        Optional<Item> itemO = itemStorage.getItemById(itemId);
        if (user != getItemFromOptionalOrThrow(itemO, itemId).getOwner()) {
            throw new ItemExistException("User is not correct for item. (user ID = " + userId
                                            + ", item ID = " + itemId);
        }
        Item item = itemMapper.toItem(checkItemDtoField(itemDto, itemId, itemO), user, null);
        Item itemToReturn = itemStorage.updateItem(item);
        log.info("Succesfully item update and saved: " + itemToReturn);
        return ItemMapper.toItemDto(itemToReturn);
    }

    @Override
    public Item getItemByIdOrThrow(int itemId) {
            Optional<Item> itemO = itemStorage.getItemById(itemId);
            log.info("Return item - " + getItemFromOptionalOrThrow(itemO, itemId));
            return getItemFromOptionalOrThrow(itemO, itemId);
    }

    @Override
    public List<ItemInfoDto> getItemsInfoDtoByUserId(int userId) {
        userService.getUserByIdOrThrow(userId);
        List<Item> items = itemStorage.getItemsByUserId(userId);
        List<ItemInfoDto> itemsInfoDto = new ArrayList<>();
        for (Item item : items) {
            itemsInfoDto.add(ItemMapper.toItemInfoDto(item, new ArrayList<>(), new ArrayList<>()));
        }
        log.info("Return item list: " + items);
        return itemsInfoDto;
    }

    @Override
    public ItemInfoDto getItemInfoDtoByIdOrThrow(int itemId, int userId) {
        Optional<Item> itemO = itemStorage.getItemById(itemId);
        log.info("Succesfully found item: " + getItemFromOptionalOrThrow(itemO, itemId));
        return ItemMapper.toItemInfoDto(getItemFromOptionalOrThrow(itemO, itemId), new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public List<ItemDto> getItemsBySubstring(String text) {
        if (text.isEmpty()) {
            log.info("The text is empty");
            return new ArrayList<>();
        }
        List<Item> items = itemStorage.getItemsBySubstring(text.toLowerCase());
        log.info("Succesfully found items: " + items);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        log.info("Return items list: " + items);
        return itemsDto;
    }

    @Override
    public CommentDto createComment(int itemId, int userId, CommentDto commentDto) {
        return null;
    }

    private ItemDto checkItemDtoField(ItemDto itemDto, int itemId, Optional<Item> itemO) {
        itemDto.setId(itemId);
        if (itemDto.getName() == null) {
            itemDto.setName(itemO.get().getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemO.get().getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemO.get().getAvailable());
        }
        return itemDto;
    }

    private Item getItemFromOptionalOrThrow(Optional<Item> itemO, int id) {
        return itemO.orElseThrow(() -> new ItemExistException("Item with id = " + id + " not exist"));
    }
}