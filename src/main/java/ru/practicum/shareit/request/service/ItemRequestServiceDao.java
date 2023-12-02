package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestExistException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Transactional(readOnly = true)
@AllArgsConstructor
public class ItemRequestServiceDao implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = false)
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, int userId) {
        User user = UserMapper.toUser(userService.getUserByIdOrThrow(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestInfo> getAllRequest(int userId) {
        UserMapper.toUser(userService.getUserByIdOrThrow(userId));
        List<ItemRequest> itemRequestList = itemRequestRepository.getAllByUserId(userId);
        List<Integer> itemRequestIdList = itemRequestList.stream().map(iR -> iR.getId()).collect(Collectors.toList());
        List<ItemDto> itemDtoList = itemRepository.findAllByItemRequestId(itemRequestIdList).stream()
                .map(i -> ItemMapper.toItemDto(i)).collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestInfoList(itemRequestList, itemDtoList);
    }

    @Override
    public List<ItemRequestInfo> getAllRequestsPagination(int userId, Pageable pageable) {
        UserMapper.toUser(userService.getUserByIdOrThrow(userId));
        List<ItemRequest> itemRequestList = itemRequestRepository.getAllAndPageable(userId, pageable);
        List<Integer> itemRequestIdList = itemRequestList.stream().map(iR -> iR.getId()).collect(Collectors.toList());
        List<ItemDto> itemDtoList = itemRepository.findAllByItemRequestId(itemRequestIdList).stream()
                .map(i -> ItemMapper.toItemDto(i)).collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestInfoList(itemRequestList, itemDtoList);
    }

    @Override
    public ItemRequestInfo getRequestById(int userId, int requestId) {
        UserMapper.toUser(userService.getUserByIdOrThrow(userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestExistException("Request with ID - " + requestId + " not found"));
        List<Integer> itemRequestIdList = List.of(itemRequest.getId());
        List<ItemDto> itemDtoList = itemRepository.findAllByItemRequestId(itemRequestIdList).stream()
                .map(i -> ItemMapper.toItemDto(i)).collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestInfo(itemRequest, itemDtoList);
    }
}
