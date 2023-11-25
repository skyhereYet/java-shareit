package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    User update(User user);

    void delete(int id);

    Optional<User> getUserById(int id);

    List<User> getUsers();

    Optional<User> userExist(User user);

    Boolean emailExist(User user);
}