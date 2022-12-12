package ru.practicum.shareit.item.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemRepository;

@Repository
public class ItemDBStorageImpl {
    private final ItemRepository repository;

    @Autowired
    public ItemDBStorageImpl(ItemRepository repository) {
        this.repository = repository;
    }

}
