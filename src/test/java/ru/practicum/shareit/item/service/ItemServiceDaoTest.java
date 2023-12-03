package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.exception.ItemExistException;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Test: Item service")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServiceDaoTest {

    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private final ItemService itemService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final ItemRequestService itemRequestService;

    @Test
    @DisplayName("ItemServiceDao: method - createItem (should_createItem_successfully)")
    @Order(value = 1)
    @Rollback(value = false)
    void should_createItem_successfully() {
        UserDto userDto = new UserDto(0, "First user", "itemServiceDao@email.com");
        UserDto userDao = userService.createOrThrow(userDto);
        UserDto userRequestDtoDao = userService.createOrThrow(new UserDto(0, "Fo request", "itemRequest@email.com"));
        ItemRequest itemRequest = new ItemRequest(0, "Black angle_new", null, null);
        itemRequestService.createRequest(
                ItemRequestMapper.toItemRequestDto(itemRequest), userRequestDtoDao.getId());

        List<ItemRequestInfo> ir = itemRequestService.getAllRequest(userRequestDtoDao.getId());
        ItemDto itemDto = new ItemDto();
        itemDto.setId(0);
        itemDto.setName("Black angle");
        itemDto.setDescription("Black angle and coal");
        itemDto.setAvailable(true);
        itemDto.setRequestId(ir.get(0).getId());
        ItemDto itemDtoDao = itemService.createItem(itemDto, userDao.getId());

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoDao.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoDao.getId()));
        assertThat(item.getDescription(), equalTo(itemDtoDao.getDescription()));
        assertThat(item.getName(), equalTo(itemDtoDao.getName()));
        assertThat(item.getAvailable(), equalTo(itemDtoDao.getAvailable()));
    }

    @Test
    @DisplayName("ItemServiceDao: method - updateItem (should_updateItem_successfully)")
    @Order(value = 2)
    @Rollback(value = false)
    void should_updateItem_successfully() {
        UserDto userDto = new UserDto(0, "First user", "should_updateItem123@email.com");
        UserDto userDao = userService.createOrThrow(userDto);
        UserDto userRequestDtoDao = userService.createOrThrow(new UserDto(0, "For request", "should_updateItem_successfully@email.com"));
        ItemRequest itemRequest = new ItemRequest(0, "Black angle_new", null, null);
        itemRequestService.createRequest(
                ItemRequestMapper.toItemRequestDto(itemRequest), userRequestDtoDao.getId());

        List<ItemRequestInfo> ir = itemRequestService.getAllRequest(userRequestDtoDao.getId());
        ItemDto itemDto = new ItemDto();
        itemDto.setId(0);
        itemDto.setName("Black angle");
        itemDto.setDescription("Black angle and coal");
        itemDto.setAvailable(true);
        itemDto.setRequestId(ir.get(0).getId());
        ItemDto itemDtoDao = itemService.createItem(itemDto, userDao.getId());

        ItemDto itemDtoSearch = itemService.getItemsBySubstring("Black angle").get(0);
        itemDtoDao = itemService.updateItem(itemDto, itemDtoDao.getId(), userDao.getId());
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemQuery = query.setParameter("id", itemDtoDao.getId()).getSingleResult();
        assertThat(itemQuery.getAvailable(), equalTo(itemDtoDao.getAvailable()));
        assertThrows(UserExistException.class, () -> {
            itemService.updateItem(itemDtoSearch, 10, 10);
        });
        assertThrows(ItemExistException.class, () -> {
            itemService.updateItem(itemDtoSearch, 11111, userDao.getId());
        });
    }

    @Test
    @DisplayName("ItemServiceDao: method - getItemByIdOrThrow (should_getItemByIdOrThrow_successfully)")
    @Order(value = 3)
    @Rollback(value = false)
    void should_getItemByIdOrThrow_successfully() {
        ItemDto itemDto = itemService.getItemsBySubstring("Black angle").get(0);
        Item item = itemService.getItemByIdOrThrow(itemDto.getId());
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemQuery = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(itemQuery.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemQuery.getName(), equalTo(item.getName()));
    }

    @Test
    @DisplayName("ItemServiceDao: method - getItemsInfoDtoByUserId (should_getItemsInfoDtoByUserId_successfully)")
    @Order(value = 4)
    @Rollback(value = false)
    void should_getItemsInfoDtoByUserId_successfully() {
        UserDto userDto = userService.getUsers().get(0);
        List<ItemInfoDto> itemInfoDtoList = itemService.getItemsInfoDtoByUserId(userDto.getId());
        assertThat(itemInfoDtoList.size(), equalTo(1));
    }

    @Test
    @DisplayName("ItemServiceDao: method - getItemInfoDtoByIdOrThrow (should_getItemInfoDtoByIdOrThrow_successfully)")
    @Order(value = 5)
    @Rollback(value = false)
    void should_getItemInfoDtoByIdOrThrow_successfully() {
        ItemDto itemDto = itemService.getItemsBySubstring("Black angle").get(0);
        UserDto userDto = userService.getUsers().get(0);
        ItemInfoDto itemInfoDto = itemService.getItemInfoDtoByIdOrThrow(itemDto.getId(), userDto.getId());
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemQuery = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(itemQuery.getAvailable(), equalTo(itemInfoDto.getAvailable()));
        assertThat(itemQuery.getName(), equalTo(itemInfoDto.getName()));
    }

    @Test
    @DisplayName("ItemServiceDao: method - getItemsBySubstring (should_getItemsBySubstring_successfully)")
    @Order(value = 6)
    @Rollback(value = false)
    void should_getItemsBySubstring_successfully() {
        List<ItemDto> itemDtoList = itemService.getItemsBySubstring("angle");
        assertThat(itemDtoList.size(), equalTo(2));
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.description = :description", Item.class);
        List<Item> itemQuery = query.setParameter("description", itemDtoList.get(0).getDescription()).getResultList();
        assertThat(itemQuery.size(), equalTo(1));

        itemDtoList = itemService.getItemsBySubstring("");
        assertThat(itemDtoList.size(), equalTo(0));
    }

    @Test
    @DisplayName("ItemServiceDao: method - createComment (should_createComment_successfully)")
    @Order(value = 7)
    @Rollback(value = false)
    void should_createComment_successfully() {
        UserDto userDto = new UserDto(0, "First user", "commented@email.com");
        UserDto userDao = userService.createOrThrow(userDto);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(0);
        itemDto.setName("Black angle");
        itemDto.setDescription("Black angle and coal");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);
        ItemDto itemDtoDao = itemService.createItem(itemDto, userDao.getId());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("text comment");

        assertThrows(ItemExistException.class, () -> {
            itemService.createComment(1111, userDao.getId(), commentDto);
        });
    }
}