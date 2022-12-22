package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.page.CustomRequestPage;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserRepository userRepository;

    private final ModelMapper<ItemRequest, ItemRequestDtoResponse> itemRequestDtoResponseMapper;
    private final ModelMapper<ItemRequest, ItemRequestDto> itemRequestDtoMapper;
    private final UserService userService;

    @Override
    public ItemRequestDtoResponse createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        userService.existsUserByUserIdOrThrow(userId);

        ItemRequest itemRequest = itemRequestDtoMapper.mapFromDto(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userRepository.getReferenceById(userId));

        return itemRequestDtoResponseMapper.mapToDto(repository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoResponse> getUserRequests(long userId) {
        userService.existsUserByUserIdOrThrow(userId);

        return mapItemRequestList(
                repository.getByRequestor_Id(userId, Sort.by("created").descending())
        );
    }

    @Override
    public List<ItemRequestDtoResponse> getOtherUserRequests(long userId, int from, int size) {
        userService.existsUserByUserIdOrThrow(userId);
        Pageable requestPage = new CustomRequestPage(from, size, Sort.by("created").descending());

        return mapItemRequestList(
                repository.getOtherUserRequests(userId, requestPage)
        );
    }

    @Override
    public ItemRequestDtoResponse getById(long userId, long id) {
        existsItemRequestOrThrow(id);
        userService.existsUserByUserIdOrThrow(userId);

        return itemRequestDtoResponseMapper.mapToDto(repository.getReferenceById(id));
    }

    @Override
    public void existsItemRequestOrThrow(long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Item request not found.");
        }
    }

    private List<ItemRequestDtoResponse> mapItemRequestList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(itemRequestDtoResponseMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
