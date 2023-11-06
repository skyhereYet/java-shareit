package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Item {
    private int id;
    @NotEmpty(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private final String name;
    @NotEmpty(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private final String description;
    @NotEmpty(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private final Boolean available;
    @NotNull(groups = {Create.class})
    private final User owner;
}
