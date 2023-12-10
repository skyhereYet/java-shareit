package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, int userId);

    List<ItemRequestInfo> getAllRequest(int userId);

    List<ItemRequestInfo> getAllRequestsPagination(int userId, Pageable pageable);

    ItemRequestInfo getRequestById(int userId, int requestId);
}
