package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.mapper.ModelMapperList;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ItemRequestDtoResponseMapper implements ModelMapperList<ItemRequest, ItemRequestDtoResponse> {

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
        fillProperties(itemRequest, itemRequestDtoResponse);

        itemRequestDtoResponse.setItems(
                itemRepository.getItemsByItemRequest_Id(itemRequest.getId()).stream()
                        .map(itemMapper::mapToDto)
                        .collect(Collectors.toList())
        );

        return itemRequestDtoResponse;
    }

    @Override
    public List<ItemRequest> mapFromDto(List<ItemRequestDtoResponse> dtoList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ItemRequestDtoResponse> mapToDto(List<ItemRequest> entities) {

        List<Item> items = itemRepository.getItemsByItemRequestIsIn(entities);
        Map<Long, List<Item>> itemsMap = new HashMap<>();
        for (Item item: items) {
            long itemRequestId = item.getItemRequest().getId();
            List<Item> itemsByRequest = itemsMap.getOrDefault(itemRequestId, new ArrayList<>());
            itemsByRequest.add(item);

            itemsMap.put(itemRequestId, itemsByRequest);
        }

        return entities.stream()
                .map(itemRequest -> {
                    ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
                    fillProperties(itemRequest, itemRequestDtoResponse);

                    itemRequestDtoResponse.setItems(
                           itemsMap.getOrDefault(itemRequestDtoResponse.getId(), new ArrayList<>())
                                   .stream()
                                   .map(itemMapper::mapToDto)
                                   .collect(Collectors.toList())
                    );
                    return itemRequestDtoResponse;
                }).collect(Collectors.toList());
    }

    private void fillProperties(ItemRequest source, ItemRequestDtoResponse destination) {
        destination.setId(source.getId());
        destination.setDescription(source.getDescription());
        destination.setCreated(source.getCreated());
    }
}
