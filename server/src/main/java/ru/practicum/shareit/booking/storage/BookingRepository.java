package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query ("select b " +
            "from Booking as b " +
            "where b.booker.id = :userId " +
            "order by b.start DESC")
    List<Booking> findAllByBooker(int userId, Pageable pageable);

    @Query ("select b " +
            "from Booking as b " +
            "where b.booker.id = :userId and b.start >= :dateTime " +
            "order by b.start DESC")
    List<Booking> findAllByBookerAndStartGreaterThan(int userId, LocalDateTime dateTime, Pageable pageable);

    @Query ("select b " +
            "from Booking as b " +
            "where b.booker.id = :userId and b.end <= :dateTime " +
            "order by b.start DESC")
    List<Booking> findAllByBookerAndStartBefore(int userId, LocalDateTime dateTime, Pageable pageable);

    @Query ("select b " +
            "from Booking as b " +
            "where b.booker.id = :userId and b.status = :state " +
            "order by b.start DESC")
    List<Booking> findAllByBookerAndStatus(int userId, BookingStatus state, Pageable pageable);

    @Query ("select b " +
            "from Booking as b " +
            "where (b.item.owner.id = :userId and b.status = :state)")
    List<Booking> findAllByItemOwnerAndStatus(int userId, BookingStatus state, Pageable pageable);

    @Query ("select b " +
            "from Booking as b " +
            "where (b.booker.id = :userId and b.item.id = :itemId and b.end <= :dateTime) " +
            "order by b.start desc ")
    List<Booking> findAllByItemAndBookerAndEndBeforeOrderByStart(int userId, int itemId, LocalDateTime dateTime);

    @Query ("select b " +
            "from Booking as b " +
            "where b.item.owner.id = :userId " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByItemOwnerStartDescPageable(int userId, Pageable pageable);

    @Query ("select b " +
            "from Booking as b " +
            "where b.item.owner.id = :userId " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByItemOwnerStartDesc(int userId);

    @Query ("select b " +
            "from Booking as b " +
            "where b.item.owner.id = :userId and b.start > :dateTime " +
            "order by b.start DESC ")
    List<Booking> findAllByItemOwnerAndStartGreaterThanOrderByStart(int userId, LocalDateTime dateTime, Pageable pageable);

    @Query ("select b " +
            "from Booking as b " +
            "where b.item.owner.id = :userId and b.end <= :dateTime " +
            "order by b.start DESC ")
    List<Booking> findAllByItemOwnerAndStartBefore(int userId, LocalDateTime dateTime, Pageable pageable);

    @Query ("select b " +
            "from Booking as b " +
            "where b.item.id = :itemId and b.status = :status " +
            "order by b.start desc ")
    List<Booking> findAllByItem_IdAndStatusOrderByStart(int itemId, BookingStatus status);

    @Query("select b " +
            "from Booking as b  " +
            "where b.booker.id = :userId and b.end >= :dateTime and b.start <= :dateTime " +
            " ORDER BY b.start asc ")
    List<Booking> findAllByBookerAndCurrent(Integer userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b " +
            "from Booking as b  " +
            "where b.item.owner.id = :userId and b.end >= :dateTime and b.start <= :dateTime " +
            " ORDER BY b.start asc ")
    List<Booking> findAllByItemOwnerAndCurrent(int userId, LocalDateTime dateTime, Pageable pageable);
}
