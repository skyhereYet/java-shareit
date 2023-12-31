package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select i " +
            "from Item as i " +
            "where i.owner.id in :userId")
    List<Item> findByOwnerId(int userId, Pageable pageable);

    @Query("select i " +
            "from Item as i " +
            "where (lower(i.description) like lower(:text) OR lower(i.name) like lower(:text))" +
            "and i.available = true")
    List<Item> findItemsByRequest(String text, Pageable pageable);

    @Query("select i " +
            "from Item as i " +
            "where i.itemRequest.id in :itemRequestList")
    List<Item> findAllByItemRequestId(List<Integer> itemRequestList);
}