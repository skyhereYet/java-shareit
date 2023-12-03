package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Test: User controller")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("should_createUser_successfully")
    void should_createUser_successfully() throws Exception {
        UserDto userDto = new UserDto(1, "First", "first@email.com");
        User user = new User(1, "First", "first@email.com");

        when(userService.createOrThrow(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @DisplayName("should_createUser_throwError")
    void should_createUser_throwError() throws Exception {
        UserDto userDto = new UserDto(1, "r", "r");
        UserDto userDto2 = new UserDto(2, "First", "first@email.com");

        when(userService.createOrThrow(any()))
                .thenThrow(ConstraintViolationException.class);

        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("should_createUser_throwEmailException")
    void should_createUser_throwEmailException() throws Exception {
        UserDto userDto1 = new UserDto(1, "First", "first@email.com");

        when(userService.createOrThrow(any()))
                .thenThrow(EmailExistException.class);

        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto1)))
                .andExpect(status().is(409));
    }

    @Test
    @DisplayName("should_getUsers")
    void should_getUsers() throws Exception {
        int userId = 1;

        List<UserDto> expected = Collections.emptyList();
        when(userService.getUsers())
                .thenReturn(expected);


        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

        verify(userService, times(1)).getUsers();
    }

    @Test
    @DisplayName("should_updateUser")
    void should_updateUser() throws Exception {
        int userId = 1;
        UserDto userDto = new UserDto(1,"First", "first@email.ru");
        User user = new User(1, "First", "first@email.ru");

        when(userService.updateOrThrow(any(), anyInt()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", user.getId())
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @DisplayName("should_deleteUser")
    void should_deleteUser() throws Exception {
        int userId = 1;
        UserDto userDto = new UserDto(1,"First", "first@email.ru");

        when(userService.deleteOrThrow(anyInt()))
                .thenReturn(userDto);

        mockMvc.perform(delete("/users/{userId}", userDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @DisplayName("should_getUserById")
    void should_getUserById() throws Exception {
        int userId = 1;
        UserDto userDto = new UserDto(1,"First", "first@email.ru");

        when(userService.getUserByIdOrThrow(anyInt()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users//{id}", userDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }
}
