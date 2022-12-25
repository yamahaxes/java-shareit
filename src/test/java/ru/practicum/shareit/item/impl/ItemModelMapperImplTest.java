package ru.practicum.shareit.item.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemModelMapperImplTest {

    @InjectMocks
    private ItemModelMapperImpl mapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void mapFromDto() {
        ItemDto itemDto = new ItemDto();

        assertDoesNotThrow(() -> mapper.mapFromDto(itemDto));
    }

    @Test
    void mapFromDto_whenRequestIdNotNull_thenSetItemRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(1L);

        when(itemRequestRepository.existsById(any()))
                .thenReturn(true);
        mapper.mapFromDto(itemDto);
        verify(itemRequestRepository).getReferenceById(1L);
    }

    @Test
    void mapFromDto_whenRequestIdNotNullAndNotExist_thenNotFoundException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(1L);

        when(itemRequestRepository.existsById(any()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> mapper.mapFromDto(itemDto));

    }

    @Test
    void mapToDto() {
        Item item = new Item();
        item.setId(1L);
        assertDoesNotThrow(() -> mapper.mapToDto(item));
    }

    @Test
    void mapToDto_whenItemRequestNotNull_thenDoesNotThrow() {
        Item item = new Item();
        item.setId(1L);
        ItemRequest itemRequest = new ItemRequest();
        item.setId(2L);
        item.setItemRequest(itemRequest);

        assertDoesNotThrow(() -> mapper.mapToDto(item));
    }
}