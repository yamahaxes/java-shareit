package ru.practicum.shareit.booking.exception;

public class UserIsOwnerException extends RuntimeException {
    public UserIsOwnerException() {
        super("User is owner of item.");
    }
}
