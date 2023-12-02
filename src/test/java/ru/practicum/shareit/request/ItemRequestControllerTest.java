package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("ItemRequest controller")
@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("ItemRequestController: method - create (should_createItemRequest_successfully)")
    void should_createItemRequest_successfully() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1, "Test request description", null, null);

        ItemRequest itemRequest = new ItemRequest(
                1,
                "Test request description",
                new User (1, "User","user@email.com"),
                LocalDateTime.now()
        );

        when(itemRequestService.createRequest(any(), anyInt()))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequest));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", itemRequest.getRequestor().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemRequest.getId()), Integer.class))
                .andExpect(jsonPath("description", is(itemRequest.getDescription())));
    }

    @Test
    @DisplayName("ItemRequestController: method - create (should_createItemRequest_throwError)")
    void should_createItemRequest_throwError() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1, null, null, null);
        ItemRequest itemRequest = new ItemRequest(
                1,
                "Test request description",
                new User (1, "User","user@email.com"),
                LocalDateTime.now()
        );

        when(itemRequestService.createRequest(any(), anyInt()))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequest));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", itemRequest.getRequestor().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("ItemRequestController: method - getAllRequest (should_getAllByUserId_successfully)")
    void should_getAllByUserId_successfully() throws Exception {
        ItemRequestInfo itemRequestInfo = new ItemRequestInfo(
                1,
                "Test request description",
                LocalDateTime.of(2023,12,01,12,01,01),
                new ArrayList<>()
        );

        when(itemRequestService.getAllRequest(anyInt()))
                .thenReturn(List.of(itemRequestInfo));

        mockMvc.perform(
                        get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestInfo))));
    }

    @Test
    @DisplayName("ItemRequestController: method - getAllPagination (should_getAllPagination_successfully)")
    void should_getAllPagination_successfully() throws Exception {
        ItemRequestInfo itemRequestInfo = new ItemRequestInfo(
                1,
                "Test request description",
                LocalDateTime.of(2023,12,01,12,01,01),
                new ArrayList<>()
        );

        when(itemRequestService.getAllRequestsPagination(anyInt(), any()))
                .thenReturn(List.of(itemRequestInfo));

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(10))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestInfo))));
    }


    @Test
    @DisplayName("ItemRequestController: method - getItemRequestById (should_getItemRequestById_successfully)")
    void should_getItemRequestById_successfully() throws Exception {
        ItemRequestInfo itemRequestInfo = new ItemRequestInfo(
                1,
                "Test request description",
                LocalDateTime.of(2023,12,01,12,01,01),
                new ArrayList<>()
        );

        when(itemRequestService.getRequestById(anyInt(), anyInt()))
                .thenReturn(itemRequestInfo);

        mockMvc.perform(
                        get("/requests/{id}", itemRequestInfo.getId())
                                .header("X-Sharer-User-Id", 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestInfo.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestInfo.getDescription())));
    }
}