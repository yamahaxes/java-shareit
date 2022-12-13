package ru.practicum.shareit.booking.exception;

public class BookingAlreadyApprovedException extends RuntimeException {
    public BookingAlreadyApprovedException() {
        super("Booking already approved.");
    }
}
