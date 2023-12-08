package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                 @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST request. Create item request. Request create: " + itemRequestDto);
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestInfo> getAllByUserId(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId) {
        log.info("GET request. Get all requests by user ID - " + userId);
        return itemRequestService.getAllRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfo> getAllPagination(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                                  @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Min(1) @RequestParam(name = "size", defaultValue = "1") Integer size) {
        log.info("GET request. Get all requests with pagination by user ID - " + userId);
        return itemRequestService.getAllRequestsPagination(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfo getItemRequestById(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                                  @Min(1) @PathVariable int requestId) {
        log.info("GET request. Get request ID - " + requestId + " by user ID - " + userId);
        return itemRequestService.getRequestById(userId,requestId);
    }
}
