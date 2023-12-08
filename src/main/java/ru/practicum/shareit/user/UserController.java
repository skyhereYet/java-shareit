package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("POST request. User create: " + userDto.toString());
        return userService.createOrThrow(userDto);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@Validated({Update.class}) @RequestBody UserDto userDto, @PathVariable int id) {
        log.info("Patch request. User update: " + userDto);
        return userService.updateOrThrow(userDto, id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET request. Get all users");
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.info("GET request. Get user by ID - " + id);
        return userService.getUserByIdOrThrow(id);
    }

    @DeleteMapping(value = "/{id}")
    public UserDto deleteUserById(@PathVariable int id) {
        log.info("DELETE request. Delete user by ID - " + id);
        return userService.deleteOrThrow(id);
    }
}
