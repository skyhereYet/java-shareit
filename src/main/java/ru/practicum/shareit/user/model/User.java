package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private int id;
    @NotEmpty(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotEmpty(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
