package ru.practicum.shareit.request.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class ItemRequestDtoMapper implements ModelMapper<ItemRequest, ItemRequestDto> {
    @Override
    public ItemRequest mapFromDto(ItemRequestDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        return itemRequest;
    }

    @Override
    public ItemRequestDto mapToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(itemRequest.getDescription());
        return itemRequestDto;
    }
}
