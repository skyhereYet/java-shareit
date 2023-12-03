package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
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
@DisplayName("Test: ItemRepository")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("ItemRequestRepository: method - findByOwnerId (should_findByOwnerId_successfully)")
    @Order(1)
    @Rollback(value = true)
    void should_findByOwnerId_successfully() {
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
        Item item = itemRepository.save(new Item(
                0,
                "item 1",
                "something",
                true,
                owner,
                itemRequest));
        List<Item> itemList = itemRepository.findByOwnerId(owner.getId());
        assertEquals(itemList.size(), 1);
        assertEquals(itemList.get(0).getId(), item.getId());
        assertEquals(itemList.get(0).getDescription(), item.getDescription());
        assertEquals(itemList.get(0).getName(), item.getName());
        assertEquals(itemList.get(0).getItemRequest().getId(), itemRequest.getId());
        assertEquals(itemList.get(0).getItemRequest().getDescription(), itemRequest.getDescription());
        assertEquals(itemList.get(0).getItemRequest().getCreated(), itemRequest.getCreated());
        assertEquals(itemList.get(0).getItemRequest().getRequestor(), user);
        assertEquals(itemList.get(0).getOwner().getId(), owner.getId());
        assertEquals(itemList.get(0).getOwner().getName(), owner.getName());
        assertEquals(itemList.get(0).getOwner().getEmail(), owner.getEmail());
    }

    @Test
    @DisplayName("ItemRequestRepository: method - findItemsByRequest (should_findItemsByRequest_successfully)")
    @Order(2)
    @Rollback(value = true)
    void should_findItemsByRequest_successfully() {
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
        Item item = itemRepository.save(new Item(
                0,
                "item 1",
                "something",
                true,
                owner,
                itemRequest));
        List<Item> itemList = itemRepository.findItemsByRequest("something");
        assertEquals(itemList.size(), 1);
        assertEquals(itemList.get(0).getId(), item.getId());
        assertEquals(itemList.get(0).getDescription(), item.getDescription());
        assertEquals(itemList.get(0).getName(), item.getName());
        assertEquals(itemList.get(0).getItemRequest().getId(), itemRequest.getId());
        assertEquals(itemList.get(0).getItemRequest().getDescription(), itemRequest.getDescription());
        assertEquals(itemList.get(0).getItemRequest().getCreated(), itemRequest.getCreated());
        assertEquals(itemList.get(0).getItemRequest().getRequestor(), user);
        assertEquals(itemList.get(0).getOwner().getId(), owner.getId());
        assertEquals(itemList.get(0).getOwner().getName(), owner.getName());
        assertEquals(itemList.get(0).getOwner().getEmail(), owner.getEmail());
    }

    @Test
    void findAllByItemRequestId() {
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
        ItemRequest itemRequest2 = itemRequestRepository.save(new ItemRequest(
                0,
                "Request for item 2",
                user,
                LocalDateTime.now()));
        Item item = itemRepository.save(new Item(
                0,
                "item 1",
                "something",
                true,
                owner,
                itemRequest));
        List<Item> itemList = itemRepository.findAllByItemRequestId(
                List.of(itemRequest.getId(), itemRequest2.getId()));
        assertEquals(itemList.size(), 1);
    }
}