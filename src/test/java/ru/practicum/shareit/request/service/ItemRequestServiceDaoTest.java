package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("ItemRequestService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemRequestServiceDaoTest {

    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private final ItemRequestService itemRequestService;
    @Autowired
    private final UserService userService;

    @Test
    @DisplayName("ItemRequestServiceDao: method - createRequest (should_createRequest_successfully)")
    @Order(value = 1)
    @Rollback(value = false)
    void should_createRequest_successfully() {
        UserDto userDto = new UserDto(0, "First user", "first@email.com");
        UserDto userDao;
        userDao = userService.createOrThrow(userDto);
        ItemRequest itemRequest = new ItemRequest(0, "Black angle", null, null);
        ItemRequestDto itemRequestDtoDao = itemRequestService.createRequest(
                ItemRequestMapper.toItemRequestDto(itemRequest), userDao.getId());

        TypedQuery<ItemRequest> query = entityManager.createQuery("Select i from ItemRequest as i where i.id = :id",
                ItemRequest.class);
        itemRequest = query.setParameter("id", itemRequestDtoDao.getId()).getSingleResult();

        assertThat(itemRequest.getId(), equalTo(itemRequestDtoDao.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDtoDao.getDescription()));
    }

    @Test
    @DisplayName("ItemRequestServiceDao: method - getAllRequest (should_getAllRequest_successfully)")
    @Order(value = 2)
    void should_getAllRequest_successfully() {
        List<UserDto> usersDtoList = userService.getUsers();
        int userId = usersDtoList.get(0).getId();
        List<ItemRequestInfo> itemRequestDtoList = itemRequestService.getAllRequest(userId);

        assertThat(itemRequestDtoList.size(), equalTo(1));
        assertThrows(UserExistException.class, () -> {
            itemRequestService.getAllRequest(10);
        });
    }

    @Test
    @DisplayName("ItemRequestServiceDao: method - getAllRequestsPagination (should_getAllRequestsPagination_successfully)")
    @Order(value = 3)
    void should_getAllRequestsPagination_successfully() {
        List<UserDto> usersDtoList = userService.getUsers();
        int userId = usersDtoList.get(0).getId();
        int from = 0;
        int size = 10;
        List<ItemRequestInfo> itemDtoList = itemRequestService.getAllRequestsPagination(
                userId,
                PageRequest.of(from / size, size));

        assertThat(itemDtoList.size(), equalTo(0));
        assertThrows(UserExistException.class, () -> {
            itemRequestService.getAllRequestsPagination(10, PageRequest.of(from / size, size));
        });
    }

    @Test
    @DisplayName("ItemRequestServiceDao: method - getRequestById (should_getRequestById_successfully)")
    @Order(value = 4)
    void should_getRequestById_successfully() {
        List<UserDto> usersDtoList = userService.getUsers();
        int userId = usersDtoList.get(0).getId();
        List<ItemRequestInfo> itemRequestDtoList = itemRequestService.getAllRequest(userId);
        int requestId = itemRequestDtoList.get(0).getId();
        ItemRequestInfo itemRequestInfo = itemRequestService.getRequestById(userId, requestId);
        TypedQuery<ItemRequest> query = entityManager.createQuery("Select i from ItemRequest i where i.id = :id",
                ItemRequest.class);
        ItemRequest itemRequestDao = query.setParameter("id", userId).getSingleResult();

        assertThat(itemRequestInfo.getDescription(), equalTo(itemRequestDao.getDescription()));
        assertThat(itemRequestInfo.getId(), equalTo(itemRequestDao.getId()));
    }
}