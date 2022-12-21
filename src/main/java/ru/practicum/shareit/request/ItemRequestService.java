package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

@Service
public interface ItemRequestService {

    ItemRequestDtoResponse createItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponse> getUserRequests(long userId);

    List<ItemRequestDtoResponse> getOtherUserRequests(long userId, int from, int size);

    ItemRequestDtoResponse getById(long userId, long id);

}
