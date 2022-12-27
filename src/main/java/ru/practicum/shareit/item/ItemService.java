package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long ownerId);

    ItemDto patch(ItemDto itemDto, long ownerId);

    ItemDto get(long userId, long itemId);

    List<ItemDto> getByUser(long ownerId, int from, int size);

    List<ItemDto> search(long userId, String text, int from, int size);

    CommentDto createComment(long userId, CommentDto commentDto);

    void existsItemByIdOrThrow(long itemId);
}
