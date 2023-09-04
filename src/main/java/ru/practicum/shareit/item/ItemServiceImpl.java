package ru.practicum.shareit.item;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;

    private int id = 1;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        User user = userService.getUserByIdOrThrow(userId);
        Item item = itemMapper.toItem(itemDto, user);
        item.setId(id++);
        Item itemToReturn = itemStorage.createItem(item);
        log.info("Succesfully item created and saved: " + itemToReturn);
        return itemMapper.toItemDto(itemToReturn);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        User user = userService.getUserByIdOrThrow(userId);
        Optional<Item> itemO = itemStorage.getItemById(itemId);
        if (user != getItemFromOptionalOrThrow(itemO, itemId).getOwner()) {
            throw new ItemExistException("User is not correct for item. (user ID = " + userId
                                            + ", item ID = " + itemId);
        }
        Item item = itemMapper.toItem(checkItemDtoField(itemDto, itemId, itemO), user);
        Item itemToReturn = itemStorage.updateItem(item);
        log.info("Succesfully item update and saved: " + itemToReturn);
        return itemMapper.toItemDto(itemToReturn);
    }

    @Override
    public Item getItemByIdOrThrow(int itemId) {
            Optional<Item> itemO = itemStorage.getItemById(itemId);
            log.info("Return item - " + getItemFromOptionalOrThrow(itemO, itemId));
            return getItemFromOptionalOrThrow(itemO, itemId);
    }

    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        userService.getUserByIdOrThrow(userId);
        List<Item> items = itemStorage.getItemsByUserId(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        log.info("Return item list: " + items);
        return itemsDto;
    }

    @Override
    public ItemDto getItemDtoByIdOrThrow(int itemId) {
        Optional<Item> itemO = itemStorage.getItemById(itemId);
        log.info("Succesfully found item: " + getItemFromOptionalOrThrow(itemO, itemId));
        return itemMapper.toItemDto(getItemFromOptionalOrThrow(itemO, itemId));
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
            itemsDto.add(itemMapper.toItemDto(item));
        }
        log.info("Return items list: " + items);
        return itemsDto;
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
