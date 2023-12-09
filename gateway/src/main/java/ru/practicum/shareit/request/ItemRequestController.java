package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                                         @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("GATEWAY: POST request. Create item request. Request create: " + itemRequestDto);
        return itemRequestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId) {
        log.info("GATEWAY: GET request. Get all requests by user ID - " + userId);
        return itemRequestClient.getAllRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPagination(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Min(1) @RequestParam(name = "size", defaultValue = "1") Integer size) {
        log.info("GATEWAY: GET request. Get all requests with pagination by user ID - " + userId);
        return itemRequestClient.getAllRequestsPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @Min(1) @PathVariable long requestId) {
        log.info("GATEWAY: GET request. Get request ID - " + requestId + " by user ID - " + userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
