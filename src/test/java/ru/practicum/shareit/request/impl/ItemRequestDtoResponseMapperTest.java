package ru.practicum.shareit.request.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestDtoResponseMapperTest {

    @InjectMocks
    private ItemRequestDtoResponseMapper mapper;

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

        when(itemRepository.getItemsByItemRequest_Id(anyLong()))
                .thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> mapper.mapToDto(itemRequest));
    }
}