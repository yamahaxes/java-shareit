package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.exception.EntityNotFoundException;

public class BookingNotFoundException extends EntityNotFoundException {
    public BookingNotFoundException() {
        super("Booking not found.");
    }
}
