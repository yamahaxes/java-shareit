package ru.practicum.shareit.request.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class ItemRequestDtoMapperTest {

    @InjectMocks
    private ItemRequestDtoMapper mapper;

    @Test
    void mapFromDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        assertDoesNotThrow(() -> mapper.mapFromDto(itemRequestDto));
    }

    @Test
    void mapToDto() {
        ItemRequest itemRequest = new ItemRequest();
        assertDoesNotThrow(() -> mapper.mapToDto(itemRequest));
    }
}