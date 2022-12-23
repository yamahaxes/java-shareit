package ru.practicum.shareit.request.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    @Mock
    private ModelMapper<ItemRequest, ItemRequestDtoResponse> itemRequestDtoResponseMapper;
    @Mock
    private ModelMapper<ItemRequest, ItemRequestDto> itemRequestDtoMapper;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        service = new ItemRequestServiceImpl(
                repository,
                userRepository,
                userService,
                itemRequestDtoResponseMapper,
                itemRequestDtoMapper
        );

        now = LocalDateTime.now();
    }

    @Test
    void createItemRequest() {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("itemRequest1");

        ItemRequest itemRequest = makeItemRequest(1);
        ItemRequestDtoResponse itemRequestDtoResponse = makeItemRequestDtoResponse(1);
        when(itemRequestDtoMapper.mapFromDto(itemRequestDto))
                .thenReturn(itemRequest);

        when(repository.save(itemRequest))
                .thenReturn(itemRequest);

        when(itemRequestDtoResponseMapper.mapToDto(itemRequest))
                .thenReturn(itemRequestDtoResponse);

        assertEquals(itemRequestDtoResponse, service.createItemRequest(1, itemRequestDto));
        assertEquals(itemRequestDtoResponse.getDescription(), itemRequestDto.getDescription());
        verify(repository, times(1)).save(itemRequest);

    }

    @Test
    void createItemRequest_whenUserNotFound_thenUserNotFoundException() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class,
                () -> service.createItemRequest(anyLong(), new ItemRequestDto()));
    }

    @Test
    void getUserRequests() {

        List<ItemRequest> itemRequestsResult = new ArrayList<>();
        List<ItemRequestDtoResponse> itemRequestDtoResponses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            itemRequestsResult.add(makeItemRequest(i));
            itemRequestDtoResponses.add(makeItemRequestDtoResponse(i));
        }

        when(itemRequestDtoResponseMapper.mapToDto(itemRequestsResult.get(0)))
                .thenReturn(itemRequestDtoResponses.get(0));
        when(itemRequestDtoResponseMapper.mapToDto(itemRequestsResult.get(1)))
                .thenReturn(itemRequestDtoResponses.get(1));

        when(repository.getByRequestor_Id(anyLong(), any()))
                .thenReturn(itemRequestsResult.stream()
                        .sorted((o1, o2) -> o2.getCreated().compareTo(o1.getCreated()))
                        .collect(Collectors.toList()));

        assertEquals(itemRequestDtoResponses.size(), service.getUserRequests(1).size());
        assertEquals(itemRequestDtoResponses.get(0), service.getUserRequests(1).get(1));
        assertEquals(itemRequestDtoResponses.get(1), service.getUserRequests(1).get(0));
    }

    @Test
    void getUserRequests_whenUserNotFound_thenNotFoundException() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class,
                () -> service.createItemRequest(anyLong(), new ItemRequestDto()));
    }

    @Test
    void getOtherUserRequests() {

        ItemRequest itemRequest1 = makeItemRequest(0);
        ItemRequest itemRequest2 = makeItemRequest(1);

        ItemRequestDtoResponse itemRequestDtoResponse1 = makeItemRequestDtoResponse(0);
        ItemRequestDtoResponse itemRequestDtoResponse2 = makeItemRequestDtoResponse(1);

        when(repository.getOtherUserRequests(anyLong(), any()))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRequestDtoResponseMapper.mapToDto(itemRequest1))
                .thenReturn(itemRequestDtoResponse1);
        when(itemRequestDtoResponseMapper.mapToDto(itemRequest2))
                .thenReturn(itemRequestDtoResponse2);

        assertEquals(List.of(itemRequestDtoResponse1, itemRequestDtoResponse2),
                service.getOtherUserRequests(1, 0, 1));

    }

    @Test
    void getOtherUserRequests_whenUserNotFound_thenNotFoundException() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class,
                () -> service.createItemRequest(anyLong(), new ItemRequestDto()));
    }

    @Test
    void getById() {
        ItemRequest itemRequest = makeItemRequest(0);
        ItemRequestDtoResponse itemRequestDtoResponse = makeItemRequestDtoResponse(0);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(itemRequest);
        when(itemRequestDtoResponseMapper.mapToDto(itemRequest))
                .thenReturn(itemRequestDtoResponse);
        when(repository.existsById(anyLong()))
                .thenReturn(true);

        assertEquals(itemRequestDtoResponse, service.getById(1, 1));
    }

    @Test
    void getById_whenUserNotFound_thenNotFoundException() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class,
                () -> service.createItemRequest(anyLong(), new ItemRequestDto()));
    }

    @Test
    void getById_whenItemRequestNotFound_thenNotFoundException() {
        when(repository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.getById(1, 1));
    }

    private ItemRequestDtoResponse makeItemRequestDtoResponse(int i) {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setDescription("itemRequest" + i);
        itemRequestDtoResponse.setCreated(now.minusDays(1).plusMinutes(i));
        itemRequestDtoResponse.setId(i + 1);

        return itemRequestDtoResponse;
    }

    private ItemRequest makeItemRequest(int i) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(now.minusDays(1).plusMinutes(i));
        itemRequest.setDescription("itemRequest" + i);
        itemRequest.setId(i + 1);

        return itemRequest;
    }
}