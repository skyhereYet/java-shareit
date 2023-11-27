package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserStorage userStorage;
    private int id = 1;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createOrThrow(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateEmail(user);
        Optional<User> userO = userStorage.userExist(user);
        if (userO.isPresent()) {
            log.info("User exist in the storage: " + userO.get());
            throw new UserExistException("User exist in the storage: " + userO.get());
        }
        user.setId(id++);
        return UserMapper.toUserDto(userStorage.save(user));
    }

    public UserDto updateOrThrow(UserDto userDto, int id) {
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        validateEmail(user);
        Optional<User> userO = userStorage.getUserById(user.getId());
        if (userO.isPresent()) {
            return UserMapper.toUserDto(userStorage.update(checkUserFields(user, userO)));
        }  else {
            log.info("User not exist in the storage: " + user);
            throw new UserExistException("User not exist in the storage: " + user);
        }
    }

    public UserDto deleteOrThrow(int id) {
        Optional<User> userO = userStorage.getUserById(id);
        if (userO.isPresent()) {
            userStorage.delete(id);
            return UserMapper.toUserDto(userO.orElseThrow(() -> new UserExistException("User not found id - " + id)));
        } else {
            log.info("User not exist in the storage with id - " + id);
            throw new UserExistException("User not exist in the storage with id - " + id);
        }
    }

    public UserDto getUserByIdOrThrow(int id) {
        try {
            Optional<User> userO = userStorage.getUserById(id);
            log.info("Return user - " + userO.orElseThrow(() -> new UserExistException("User with id = " + id
                    + " not exist")));
            return UserMapper.toUserDto(
                    userO.orElseThrow(() -> new UserExistException("User with id = " + id + " not exist")));
        } catch (NullPointerException e) {
            throw new UserExistException("User with id = " + id + " not exist");
        }
    }

    public List<UserDto> getUsers() {
        log.info("Return user storage list");
        return userStorage.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
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