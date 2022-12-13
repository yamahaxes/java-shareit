package ru.practicum.shareit.booking.exception;

public class NotFoundBookingException extends RuntimeException {
    public NotFoundBookingException() {
        super("Not found booking.");
    }
}
