package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createOrThrow(UserDto userDto);

    UserDto updateOrThrow(UserDto userDto, int id);

    UserDto deleteOrThrow(int id);

    UserDto getUserByIdOrThrow(int id);

    List<UserDto> getUsers();
}