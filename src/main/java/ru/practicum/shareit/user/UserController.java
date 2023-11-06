package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Validated({Create.class}) @RequestBody User user) {
        log.info("POST request. User create: " + user.toString());
        return userService.createOrThrow(user);
    }

    @PatchMapping(value = "/{id}")
    public User update(@Validated({Update.class}) @RequestBody User user, @PathVariable int id) {
        user.setId(id);
        log.info("Patch request. User update: " + user);
        return userService.updateOrThrow(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("GET request. Get all users");
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("GET request. Get user by ID - " + id);
        return userService.getUserByIdOrThrow(id);
    }

    @DeleteMapping(value = "/{id}")
    public User deleteUserById(@PathVariable int id) {
        log.info("DELETE request. Delete user by ID - " + id);
        return userService.deleteOrThrow(id);
    }
}
