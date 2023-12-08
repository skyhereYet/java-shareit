package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@SpringBootTest
@DisplayName("Test: CommentMapper")
class CommentMapperTest {

    @Test
    void should_toCommentDto_successfully() {
        Item item = new Item();
        item.setId(0);
        item.setName("Coal");
        item.setDescription("Very black coal");
        item.setItemRequest(null);
        item.setOwner(null);
        Comment comment = new Comment(
                0,
                "This coal is so black",
                item,
                new User(0, "User", "user@user.com"),
                LocalDateTime.now()
                );
        CommentMapper.toCommentDto(comment);
    }

    @Test
    void should_toComment_successfully() {
        Item item = new Item();
        item.setId(0);
        item.setName("Coal");
        item.setDescription("Very black coal");
        item.setItemRequest(null);
        item.setOwner(null);
        CommentDto commentDto = new CommentDto(
                0,
                "This coal is so black",
                0,
                null,
                LocalDateTime.now()
        );
        CommentMapper.toComment(commentDto, item, new User());
    }
}