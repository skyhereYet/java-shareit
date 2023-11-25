package ru.practicum.shareit.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private final ItemService itemService;

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @Min(0) int userId,
                              @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("POST request. Create item. User ID - " + userId + ", itemDto = " + itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") @Min(0) int userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable int itemId) {
        log.info("PATCH request. Update item. User ID - " + userId + ", itemDto = " + itemDto);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping
    public List<ItemInfoDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") @Min(0) int userId) {
        log.info("GET request. Get items by user ID - " + userId);
        return itemService.getItemsInfoDtoByUserId(userId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemInfoDto getItemById(@PathVariable int itemId,
                                   @RequestHeader("X-Sharer-User-Id") @Min(0) int userId) {
        log.info("GET request (getItemById). Get items by item ID - " + itemId + ", user ID - " + userId);
        return itemService.getItemInfoDtoByIdOrThrow(itemId, userId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("GET request. Search items by substring - " + text);
        return itemService.getItemsBySubstring(text);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") @Min(0) int userId,
                                    @PathVariable @Min(0) int itemId,
                                    @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("POST request. Create comment. User ID - " + userId + ", commentDto = " + commentDto);
        return itemService.createComment(itemId, userId, commentDto);
    }
}