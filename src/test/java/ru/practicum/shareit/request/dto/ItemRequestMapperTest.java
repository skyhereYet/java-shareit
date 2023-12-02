package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test: ItemRequestMapper")
class ItemRequestMapperTest {

    @Test
    void should_toItemRequestDto_successfully() {
        User requestor = new User(1,"First", "First@email.com");
        ItemRequest itemRequest = new ItemRequest(1, "Black angle", requestor, LocalDateTime.now());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    void should_toItemRequest_successfully() {
        User requestor = new User(1,"First", "First@email.com");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Black angle", requestor,
                null);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void should_toItemRequestInfoList_successfully() {
        User requestor = new User(1,"First", "First@email.com");
        ItemRequest itemRequest = new ItemRequest(1, "Black angle", requestor, LocalDateTime.now());
        List<ItemRequestInfo> itemRequestInfoList = ItemRequestMapper.toItemRequestInfoList(
                List.of(itemRequest),
                new ArrayList<>());

        assertEquals(itemRequest.getId(), itemRequestInfoList.get(0).getId());
        assertEquals(itemRequest.getDescription(), itemRequestInfoList.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestInfoList.get(0).getCreated());
        assertEquals(itemRequest.getRequestor().getName(), requestor.getName());
    }

    @Test
    void should_toItemRequestInfo_successfully() {
        User requestor = new User(1,"First", "First@email.com");
        ItemRequest itemRequest = new ItemRequest(1, "Black angle", requestor, LocalDateTime.now());
        ItemRequestInfo itemRequestInfo = ItemRequestMapper.toItemRequestInfo(
                itemRequest,
                new ArrayList<>());

        assertEquals(itemRequest.getId(), itemRequestInfo.getId());
        assertEquals(itemRequest.getDescription(), itemRequestInfo.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestInfo.getCreated());
    }
}