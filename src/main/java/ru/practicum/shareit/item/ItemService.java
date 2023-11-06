package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    Item getItemByIdOrThrow(int id);

    List<ItemDto> getItemsByUserId(int userId);

    ItemDto getItemDtoByIdOrThrow(int itemId);

    List<ItemDto> getItemsBySubstring(String text);
}
