package ru.practicum.shareit.booking.exception;

public class NoAvailableItemNotFoundException extends RuntimeException {
    public NoAvailableItemNotFoundException() {
        super("No available item found.");
    }
}
