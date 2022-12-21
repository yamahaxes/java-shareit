package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ItemModelMapperImpl implements ModelMapper<Item, ItemDto> {

    private final ModelMapper<Comment, CommentDto> commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item mapFromDto(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        if (itemDto.getRequestId() != null) {
            if (!itemRequestRepository.existsById(itemDto.getRequestId())) {
                throw new NotFoundException("Item request not found.");
            }
            item.setItemRequest(itemRequestRepository.getReferenceById(itemDto.getRequestId()));
        }

        return item;
    }

    @Override
    public ItemDto mapToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        List<CommentDto> commentsDto = item.getComments()
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentsDto);

        if (item.getItemRequest() != null) {
            itemDto.setRequestId(item.getItemRequest().getId());
        }
        return itemDto;
    }
}
