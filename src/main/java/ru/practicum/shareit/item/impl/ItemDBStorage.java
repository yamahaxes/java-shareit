package ru.practicum.shareit.item.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemRepository;

@Repository
public class ItemDBStorage {
    private final ItemRepository repository;

    @Autowired
    public ItemDBStorage(ItemRepository repository) {
        this.repository = repository;
    }

}
