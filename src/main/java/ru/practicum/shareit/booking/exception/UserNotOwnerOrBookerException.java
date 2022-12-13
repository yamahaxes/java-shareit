package ru.practicum.shareit.booking.exception;

public class UserNotOwnerOrBookerException extends RuntimeException {
    public UserNotOwnerOrBookerException() {
        super("User not owner or booker.");
    }
}
