package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Item controller")
@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("ItemController: method - createItem (should_createItem_successfully)")
    void should_createItem_successfully() throws Exception {
        ItemDto itemDto = new ItemDto(1,
                "Black angle",
                "Very black angle",
                true,
                1);

        when(itemService.createItem(any(), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("description", is(itemDto.getDescription())));
    }

    @Test
    @DisplayName("ItemController: method - createItem (should_createItem_throwException)")
    void should_createItem_throwException() throws Exception {
        ItemDto itemDto = new ItemDto(1,
                null,
                null,
                true,
                1);

        when(itemService.createItem(any(), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("ItemController: method - update (should_update_successfully)")
    void should_update_successfully() throws Exception {
        should_createItem_successfully();
        ItemDto itemDtoUpdate = new ItemDto(1,
                "Black angle",
                "Oh, one day I found it in such a dark corner and when I picked it up " +
                        "I felt a surge of super power. My hands finally moved to my shoulders",
                true,
                1);

        when(itemService.updateItem(any(), anyInt(), anyInt()))
                .thenReturn(itemDtoUpdate);

        mockMvc.perform(patch("/items/{id}", itemDtoUpdate.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDtoUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemDtoUpdate.getId()), Integer.class))
                .andExpect(jsonPath("description", is(itemDtoUpdate.getDescription())));
    }

    @Test
    @DisplayName("ItemController: method - getItemsByUserId (should_getItemsByUserId_successfully)")
    void should_getItemsByUserId_successfully() throws Exception {
        List<ItemInfoDto> list = Collections.emptyList();
        when(itemService.getItemsInfoDtoByUserId(anyInt(), any()))
                .thenReturn(list);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
        verify(itemService, times(1)).getItemsInfoDtoByUserId(anyInt(), any());
    }

    @Test
    @DisplayName("ItemController: method - getItemById (should_getItemById_successfully)")
    void should_getItemById_successfully() throws Exception {
        ItemInfoDto itemInfoDto = new ItemInfoDto(1,
                "Black coal",
                "My hands are still black",
                true,
                null,
                null,
                new ArrayList<>());

        when(itemService.getItemInfoDtoByIdOrThrow(anyInt(), anyInt()))
                .thenReturn(itemInfoDto);
        mockMvc.perform(get("/items//{id}", itemInfoDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDto.getDescription())));
    }

    @Test
    @DisplayName("ItemController: method - searchItem (should_searchItem_successfully)")
    void should_searchItem_successfully() throws Exception {
        int from = 0;
        int size = 10;

        List<ItemDto> list = Collections.emptyList();
        when(itemService.getItemsBySubstring(any(), any()))
                .thenReturn(list);
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    @DisplayName("ItemController: method - createComment (should_createComment_successfully)")
    void should_createComment_successfully() throws Exception {
        CommentDto commentDto = new CommentDto(
                1,
                "This black angle is really fantastic",
                1,
                "Joe Smith",
                LocalDateTime.now());

        when(itemService.createComment(anyInt(), anyInt(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.item", is(commentDto.getItem()), Integer.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}