package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceDao implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = false)
    public UserDto createOrThrow(UserDto userDto) {
        return UserMapper.toUserDto(
                userRepository.save(UserMapper.toUser(userDto))
        );
    }

    @Override
    @Transactional(readOnly = false)
    public UserDto updateOrThrow(UserDto userDto, int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID = "  + id));
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = false)
    public UserDto deleteOrThrow(int id) {
        UserDto userDto = UserMapper.toUserDto(
                userRepository.findById(id)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID = "  + id)));
        userRepository.deleteById(id);
        return userDto;
    }

    @Override
    public UserDto getUserByIdOrThrow(int id) {
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID = "  + id)));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserMapper.toUserDto(u))
                .collect(Collectors.toList());
    }
}