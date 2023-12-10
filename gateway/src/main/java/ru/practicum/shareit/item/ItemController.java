package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Min;
import java.util.ArrayList;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    @Autowired
    private final ItemClient itemClient;

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                                             @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("GATEWAY: POST request. Create item. User ID - " + userId + ", itemDto = " + itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable int itemId) {
        log.info("GATEWAY: PATCH request. Update item. User ID - " + userId + ", itemDto = " + itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                                              @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Min(1) @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("GATEWAY: GET request. Get items by user ID - " + userId);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
                                   @RequestHeader("X-Sharer-User-Id") @Min(1) long userId) {
        log.info("GATEWAY: GET request (getItemById). Get items by item ID - " + itemId + ", user ID - " + userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                    @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @Min(1) @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (text.isEmpty() || text.isBlank()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        log.info("GET request. Search items by substring - " + text);
        return itemClient.getItemsBySubstring(text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                                    @PathVariable @Min(1) long itemId,
                                    @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("POST request. Create comment. User ID - " + userId + ", commentDto = " + commentDto);
        return itemClient.createComment(itemId, userId, commentDto);
    }
}