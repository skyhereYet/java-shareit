package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    Item getItemByIdOrThrow(int id);

    List<ItemInfoDto> getItemsInfoDtoByUserId(int userId);

    ItemInfoDto getItemInfoDtoByIdOrThrow(int itemId, int userId);

    List<ItemDto> getItemsBySubstring(String text);

    CommentDto createComment(int itemId, int userId, CommentDto commentDto);
}
