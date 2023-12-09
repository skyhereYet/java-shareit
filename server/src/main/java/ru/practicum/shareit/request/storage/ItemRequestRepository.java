package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query ("select req " +
            "from ItemRequest as req " +
            "where req.requestor.id = :userId " +
            "order by req.created desc")
    List<ItemRequest> getAllByUserId(int userId);

    @Query ("select req " +
            "from ItemRequest as req " +
            "where req.requestor.id <> :userId " +
            "order by req.created desc")
    List<ItemRequest> getAllAndPageable(int userId, Pageable pageable);
}
