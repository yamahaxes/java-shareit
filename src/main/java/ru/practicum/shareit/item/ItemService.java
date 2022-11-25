package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long ownerId);

    ItemDto patch(ItemDto itemDto, long ownerId);

    ItemDto get(long id);

    List<ItemDto> getByUser(long ownerId);

    List<ItemDto> search(String text);
}
