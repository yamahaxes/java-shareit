package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException() {
        super("User not found.");
    }
}
