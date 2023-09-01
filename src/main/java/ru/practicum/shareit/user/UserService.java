package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;
    private int id = 1;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createOrThrow(User user) {
        validateEmail(user);
        Optional<User> userO = userStorage.userExist(user);
        if (userO.isPresent()) {
            log.info("User exist in the storage: " + userO.get());
            throw new UserExistException("User exist in the storage: " + userO.get());
        }
        user.setId(id++);
        return userStorage.save(user);
    }

    public User updateOrThrow(User user) {
        validateEmail(user);
        Optional<User> userO = userStorage.getUserById(user.getId());
        if (userO.isPresent()) {
            return userStorage.update(checkUserFields(user, userO));
        }  else {
            log.info("User not exist in the storage: " + user);
            throw new UserExistException("User not exist in the storage: " + user);
        }
    }

    public User deleteOrThrow(int id) {
        Optional<User> userO = userStorage.getUserById(id);
        if (userO.isPresent()) {
            return userStorage.delete(id);
        } else {
            log.info("User not exist in the storage with id - " + id);
            throw new UserExistException("User not exist in the storage with id - " + id);
        }
    }

    public User getUserByIdOrThrow(int id) {
        try {
            Optional<User> userO = userStorage.getUserById(id);
            log.info("Return user - " + userO.get());
            return userO.orElseThrow(() -> new UserExistException("User with id = " + id + " not exist"));
        } catch (NullPointerException e) {
            throw new UserExistException("User with id = " + id + " not exist");
        }
    }

    public List<User> getUsers() {
        log.info("Return user storage list");
        return userStorage.getUsers();
    }

    private User checkUserFields(User user, Optional<User> userO) {
        if (user.getName() == null) {
            user.setName(userO.get().getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userO.get().getEmail());
        }
        return user;
    }

    private void validateEmail(User user) {
        if (userStorage.emailExist(user)) {
            throw new EmailExistException("User with email - " + user.getEmail() + " is exist");
        }
    }
}
