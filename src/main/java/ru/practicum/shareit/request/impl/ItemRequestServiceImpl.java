package ru.practicum.shareit.request.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public ItemRequestDtoResponse createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        return null;
    }

    @Override
    public List<ItemRequestDtoResponse> getUserRequests(long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDtoResponse> getOtherUserRequests(long userId, int from, int size) {
        return null;
    }

    @Override
    public ItemRequestDtoResponse getById(long id) {
        return null;
    }
}
