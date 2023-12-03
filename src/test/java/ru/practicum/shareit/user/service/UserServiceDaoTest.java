package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Test: User service")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceDaoTest {

    private final EntityManager entityManager;
    private final UserService userService;

    @Test
    @DisplayName("UserServiceDao: method - createOrThrow (should_createOrThrow_successfully)")
    @Order(value = 1)
    @Rollback(value = false)
    void should_createOrThrow_successfully() {
        UserDto userDto = new UserDto(0, "First user", "firstuser@email.com");
        UserDto userDao;
        userDao = userService.createOrThrow(userDto);
        TypedQuery<User> query = entityManager.createQuery("Select i from User i where i.id = :id", User.class);
        User userRepository = query.setParameter("id", userDao.getId()).getSingleResult();

        assertThat(userRepository.getId(), equalTo(userDao.getId()));
        assertThat(userRepository.getName(), equalTo(userDao.getName()));
        assertThat(userRepository.getEmail(), equalTo(userDao.getEmail()));
    }

    @Test
    @DisplayName("UserServiceDao: method - updateOrThrow (should_updateOrThrow_successfully)")
    @Order(value = 2)
    void should_updateOrThrow_successfully() {
        //userService.createOrThrow(new UserDto(0, "First user", "first@email.com"));
        List<UserDto> users = userService.getUsers();
        int userId = users.get(0).getId();
        UserDto userDto = new UserDto(userId,"updatename", "update@email.com");

        UserDto userDao = userService.updateOrThrow(userDto, userId);
        TypedQuery<User> query = entityManager.createQuery("Select i from User i where i.id = :id", User.class);
        User userRepository = query.setParameter("id", userId).getSingleResult();

        assertThat(userRepository.getId(), equalTo(userDao.getId()));
        assertThat(userRepository.getName(), equalTo(userDto.getName()));
        assertThat(userRepository.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @DisplayName("UserServiceDao: method - getUserByIdOrThrow (should_getUserByIdOrThrow_successfully)")
    @Order(value = 3)
    void should_getUserByIdOrThrow_successfully() {
        //userService.createOrThrow(new UserDto(0, "First user", "first@email.com"));
        List<UserDto> users = userService.getUsers();
        int userId = users.get(0).getId();
        TypedQuery<User> query = entityManager.createQuery("Select i from User i where i.id = :id", User.class);
        User userOut = query.setParameter("id", userId).getSingleResult();
        UserDto userDao = userService.getUserByIdOrThrow(userId);
        assertThat(userDao.getId(), equalTo(userOut.getId()));
        assertThat(userDao.getName(), equalTo(userOut.getName()));
        assertThat(userDao.getEmail(), equalTo(userOut.getEmail()));
    }

    @Test
    @DisplayName("UserServiceDao: method - getUsers (should_getUsers_successfully)")
    @Order(value = 4)
    void should_getUsers_successfully() {
        //userService.createOrThrow(new UserDto(0, "First user", "first@email.com"));
        List<UserDto> users = userService.getUsers();
        assertThat(users.size(), equalTo(7));
        TypedQuery<User> query = entityManager.createQuery("Select i from User i ", User.class);
        List<User> userDao = query.getResultList();
        assertThat(userDao.size(), equalTo(users.size()));
    }

    @Test
    @DisplayName("UserServiceDao: method - deleteOrThrow (should_deleteOrThrow_successfully)")
    @Order(value = 5)
    void should_deleteOrThrow_successfully() {
        //userService.createOrThrow(new UserDto(0, "First user", "first@email.com"));
        List<UserDto> users = userService.getUsers();
        int userId = users.get(0).getId();
        userService.deleteOrThrow(userId);
        Assertions.assertThrows(UserExistException.class, () -> userService.getUserByIdOrThrow(userId));
    }
}