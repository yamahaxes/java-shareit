package ru.practicum.shareit.request.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestDtoResponseMapperTest {

    @InjectMocks
    private ItemRequestDtoResponseMapper mapper;

    @Mock
    private ModelMapper<Item, ItemDto> itemMapper;

    @Mock
    private ItemRepository itemRepository;

    @Test
    void mapFromDto() {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();

        assertDoesNotThrow(() -> mapper.mapFromDto(itemRequestDtoResponse));
    }

    @Test
    void mapToDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        when(itemRepository.getItemsByItemRequest_IdOrderById(anyLong()))
                .thenReturn(new ArrayList<>());
        assertDoesNotThrow(() -> mapper.mapToDto(itemRequest));
        verify(itemMapper, times(0)).mapToDto(new Item());
    }

    @Test
    void mapToDtoList() {

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);

        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);

        when(itemRepository.getItemsByItemRequestIsInOrderById(anyList()))
                .thenReturn(new ArrayList<>());
        assertDoesNotThrow(() -> mapper.mapToDto(itemRequests));

    }

    @Test
    void mapFromDtoList() {
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.mapFromDto(new ArrayList<>()));
    }
}