package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                user,
                LocalDateTime.now());
    }

    public static List<ItemRequestInfo> toItemRequestInfoList(List<ItemRequest> itemRequestList,
                                                              List<ItemDto> itemDtoList) {
        List<ItemRequestInfo> itemRequestInfoList = new ArrayList<>();
        for (ItemRequest itemRequest: itemRequestList) {
            List<ItemDto> itemDtoListTemp = itemDtoList.stream()
                    .filter(iDto -> iDto.getRequestId() == itemRequest.getRequestor().getId())
                    .collect(Collectors.toList());
            itemRequestInfoList.add(toItemRequestInfo(itemRequest, itemDtoListTemp));
        }
        return itemRequestInfoList;
    }

    public static ItemRequestInfo toItemRequestInfo(ItemRequest itemRequest,
                                                              List<ItemDto> itemDtoList) {
        return new ItemRequestInfo(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemDtoList);
    }
}
