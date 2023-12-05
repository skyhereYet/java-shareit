package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

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
@DisplayName("Test: ItemRequestService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemRequestServiceDaoTest {

    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private final ItemRequestService itemRequestService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserRepository userRepository;

    @Test
    @DisplayName("ItemRequestServiceDao: method - createRequest (should_createRequest_successfully)")
    @Order(value = 1)
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
        UserDto userDto = new UserDto(0, "First user", "getAllRequest@email.com");
        UserDto userDao;
        userDao = userService.createOrThrow(userDto);
        ItemRequest itemRequest = new ItemRequest(0, "Black angle", null, null);
        ItemRequestDto itemRequestDtoDao = itemRequestService.createRequest(
                ItemRequestMapper.toItemRequestDto(itemRequest), userDao.getId());
        List<ItemRequestInfo> itemRequestDtoList = itemRequestService.getAllRequest(userDao.getId());

        assertThat(itemRequestDtoList.size(), equalTo(1));
        assertThrows(UserExistException.class, () -> {
            itemRequestService.getAllRequest(10);
        });
    }

    @Test
    @DisplayName("ItemRequestServiceDao: method - getAllRequestsPagination (should_getAllRequestsPagination_successfully)")
    @Order(value = 3)
    void should_getAllRequestsPagination_successfully() {
        User user = new User(0, "First user", "getAllRequestsPagination@email.com");
        User userDao = userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(0, "Black angle1", userDao, null);
        ItemRequestDto itemRequestDtoDao = itemRequestService.createRequest(
                ItemRequestMapper.toItemRequestDto(itemRequest), userDao.getId());

        int from = 0;
        int size = 10;
        List<ItemRequestInfo> itemDtoList = itemRequestService.getAllRequestsPagination(
                userDao.getId(),
                PageRequest.of(from / size, size));

        assertThat(itemDtoList.size(), equalTo(0));
        assertThrows(UserExistException.class, () -> {
            itemRequestService.getAllRequestsPagination(11111, PageRequest.of(from / size, size));
        });
    }

    @Test
    @DisplayName("ItemRequestServiceDao: method - getRequestById (should_getRequestById_successfully)")
    @Order(value = 4)
    void should_getRequestById_successfully() {
        UserDto userDto = new UserDto(0, "First user", "getRequestById@email.com");
        UserDto userDao;
        userDao = userService.createOrThrow(userDto);
        ItemRequest itemRequest = new ItemRequest(0, "Black angle", null, null);
        ItemRequestDto itemRequestDtoDao = itemRequestService.createRequest(
                ItemRequestMapper.toItemRequestDto(itemRequest), userDao.getId());

        List<ItemRequestInfo> itemRequestDtoList = itemRequestService.getAllRequest(userDao.getId());
        int requestId = itemRequestDtoList.get(0).getId();
        ItemRequestInfo itemRequestInfo = itemRequestService.getRequestById(userDao.getId(), requestId);
        TypedQuery<ItemRequest> query = entityManager.createQuery("Select i from ItemRequest i where i.id = :id",
                ItemRequest.class);
        ItemRequest itemRequestDao = query.setParameter("id", itemRequestDtoDao.getId()).getSingleResult();

        assertThat(itemRequestInfo.getDescription(), equalTo(itemRequestDao.getDescription()));
        assertThat(itemRequestInfo.getId(), equalTo(itemRequestDao.getId()));
    }
}