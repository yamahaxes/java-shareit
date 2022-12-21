package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ItemRequestDtoResponseMapper implements ModelMapper<ItemRequest, ItemRequestDtoResponse> {

    private final ItemRepository itemRepository;
    private final ModelMapper<Item, ItemDto> itemMapper;

    @Override
    public ItemRequest mapFromDto(ItemRequestDtoResponse dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(dto.getId());
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(dto.getCreated());
        return itemRequest;
    }

    @Override
    public ItemRequestDtoResponse mapToDto(ItemRequest itemRequest) {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setId(itemRequest.getId());
        itemRequestDtoResponse.setDescription(itemRequest.getDescription());
        itemRequestDtoResponse.setCreated(itemRequest.getCreated());

        itemRequestDtoResponse.setItems(
                itemRepository.getItemsByItemRequest_Id(itemRequest.getId()).stream()
                        .map(itemMapper::mapToDto)
                        .collect(Collectors.toList())
        );

        return itemRequestDtoResponse;
    }
}
