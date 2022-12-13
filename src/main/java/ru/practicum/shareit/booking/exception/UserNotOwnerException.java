package ru.practicum.shareit.booking.exception;

public class UserNotOwnerException extends RuntimeException {
    public UserNotOwnerException() {
        super("User not owner.");
    }
}
