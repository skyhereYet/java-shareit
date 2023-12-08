package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Test: CommentRepository")
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    void findCommentByItemOrderByCreated() {
        User user = userRepository.save(new User(
                0,
                "First",
                "first@email.com"));
        User owner = userRepository.save(new User(
                0,
                "Owner",
                "owner@email.com"));
        ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(
                0,
                "Request for item",
                user,
                LocalDateTime.now()));
        Item item1 = itemRepository.save(new Item(
                0,
                "item 1",
                "something",
                true,
                owner,
                itemRequest));
        Item item2 = itemRepository.save(new Item(
                0,
                "item 2",
                "something",
                true,
                owner,
                itemRequest));
        Comment comment1 = commentRepository.save(new Comment(
                0,
                "This is fantastic",
                item1,
                user,
                LocalDateTime.now()
                ));
        Comment comment2 = new Comment();
        comment2.setId(0);
        comment2.setText("So bad, I'm hungry");
        comment2.setItem(item1);
        comment2.setAuthor(user);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);
        List<Comment> itemList = commentRepository.findCommentByItemOrderByCreated(item1);
        assertEquals(itemList.size(), 2);
        assertEquals(itemList.get(0).getId(), comment1.getId());
        assertEquals(itemList.get(0).getText(), comment1.getText());
        assertEquals(itemList.get(0).getItem(), comment1.getItem());
        assertEquals(itemList.get(0).getAuthor(), comment1.getAuthor());
        assertEquals(itemList.get(0).getCreated(), comment1.getCreated());
    }
}