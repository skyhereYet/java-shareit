package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("ItemRepository")
class ItemRepositoryTest {

    @Test
    void findByOwnerId() {
    }

    @Test
    void findItemsByRequest() {
    }

    @Test
    void findAllByItemRequestId() {
    }
}