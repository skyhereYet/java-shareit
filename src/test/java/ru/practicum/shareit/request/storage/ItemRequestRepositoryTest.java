package ru.practicum.shareit.request.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Test: ItemRequestRepository")
class ItemRequestRepositoryTest {

    private final EntityManager entityManager;
    @Autowired
    private final ItemRequestRepository itemRequestRepository;
    @Autowired
    private final UserRepository userRepository;

    @Test
    @DisplayName("ItemRequestRepository: method - getAllByUserId (should_getAllByUserId_successfully)")
    @Order(1)
    @Rollback(value = true)
    void should_getAllByUserId_successfully() {
        User user = new User(1, "First", "first@email.com");
        ItemRequest itemRequest = new ItemRequest(1, "Need a brush", user, LocalDateTime.now());
        userRepository.save(user);
        itemRequestRepository.save(itemRequest);

        TypedQuery<ItemRequest> query = entityManager.createQuery(
                "Select ir from ItemRequest as ir where ir.requestor.id = :id", ItemRequest.class);
        ItemRequest itemRequestDao = query.setParameter("id", itemRequest.getId()).getSingleResult();
        assertEquals(itemRequestDao.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDao.getRequestor().getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("ItemRequestRepository: method - getAllAndPageable (should_getAllAndPageable_successfully)")
    @Order(2)
    @Rollback(value = true)
    void should_getAllAndPageable_successfully() {
        User user = new User(1, "First", "first@email.com");
        ItemRequest itemRequest = new ItemRequest(1, "Need a brush", user, LocalDateTime.now());
        userRepository.save(user);
        /*itemRequestRepository.save(itemRequest);*/

        TypedQuery<ItemRequest> query = entityManager.createQuery(
                "Select ir from ItemRequest as ir where ir.requestor.id <> :id", ItemRequest.class);
        List<ItemRequest> itemRequestDaoList = query.setParameter("id", itemRequest.getId()).getResultList();
        assertTrue(itemRequestDaoList.isEmpty());
    }
}