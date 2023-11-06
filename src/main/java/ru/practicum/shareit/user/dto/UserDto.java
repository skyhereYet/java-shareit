package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private final int id;
    private final String name;
    @Email
    @NotEmpty
    private final String email;
}
