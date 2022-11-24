package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.repository.Repository;

import java.util.List;

public interface ItemRepository extends Repository<Item> {
    List<Item> getItemsByUser(long ownerId);

    List<Item> search(String text);
}
