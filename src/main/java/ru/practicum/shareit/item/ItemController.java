package ru.practicum.shareit.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
                        @Validated({Update.class}) @RequestBody ItemDto itemDto, @PathVariable int itemId) {
        log.info("PATCH request. Update item. User ID - " + userId + ", itemDto = " + itemDto);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") @Min(0) int userId) {
        log.info("GET request. Get items by user ID - " + userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        log.info("GET request. Get items by item ID - " + itemId);
        return itemService.getItemDtoByIdOrThrow(itemId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("GET request. Search items by substring - " + text);
        return itemService.getItemsBySubstring(text);
    }
}
