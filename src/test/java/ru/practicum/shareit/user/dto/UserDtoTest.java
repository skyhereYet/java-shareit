package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Test: UserDto")
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    @DisplayName("should serialize")
    void should_serialize() throws Exception {
        UserDto userDto = new UserDto(1,"First", "First@email.com");
        var json = jacksonTester.write(userDto);
        assertThat(json).hasJsonPath("$.id");
        assertThat(json).hasJsonPath("$.name");
        assertThat(json).hasJsonPath("$.email");

        assertThat(json).extractingJsonPathValue("$.id").isEqualTo(userDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }

    @Test
    @DisplayName("should deserialize")
    void should_deserialize() throws Exception {
        final String dtoJson = "{\n" +
                "    \"id\":  \"1\",\n" +
                "    \"name\":  \"First\",\n" +
                "    \"email\": \"First@email.com\"" +
                "}";
        UserDto userDto = new UserDto(1,"First", "First@email.com");
        var dto = jacksonTester.parseObject(dtoJson);

        assertThat(dto).extracting("id").isEqualTo(userDto.getId());
        assertThat(dto).extracting("name").isEqualTo(userDto.getName());
        assertThat(dto).extracting("email").isEqualTo(userDto.getEmail());
    }
}