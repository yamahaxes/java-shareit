package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.exception.EntityNotFoundException;

public class ItemNotFoundException extends EntityNotFoundException {
    public ItemNotFoundException() {
        super("Item not found.");
    }
}
