package ru.practicum.shareit.user.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(MemoryUserStorage.class);
    private final HashMap<Integer, User> userStorage = new HashMap<>();


    @Override
    public User save(User user) {
        userStorage.put(user.getId(), user);
        log.info("Successfully add user - " + user);
        return user;
    }

    @Override
    public User update(User user) {
        userStorage.put(user.getId(), user);
        log.info("Successfully update user - " + user);
        return user;
    }

    @Override
    public User delete(int id) {
        User userToReturn = getUserById(id).get();
        userStorage.remove(id);
        log.info("Successfully delete user - " + userToReturn);
        return userToReturn;
    }

    @Override
    public Optional<User> getUserById(int id) {
        log.info("Get user by id - " + id);
        return Optional.of(userStorage.get(id));
    }

    @Override
    public List<User> getUsers() {
        log.info("Get all users");
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public Optional<User> userExist(User user) {
        if (userStorage.containsValue(user)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Boolean emailExist(User user) {
        List<User> list = userStorage.values().stream().filter(u -> u.getEmail().equals(user.getEmail())).collect(Collectors.toList());
        if (list.isEmpty()) {
            return false;
        } else if (list.get(0).getId() == user.getId()) {
            return false;
        }else {
            return true;
        }
    }
}
