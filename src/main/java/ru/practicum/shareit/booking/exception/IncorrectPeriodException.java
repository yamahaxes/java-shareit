package ru.practicum.shareit.booking.exception;

public class IncorrectPeriodException extends RuntimeException {
    public IncorrectPeriodException() {
        super("Incorrect period.");
    }
}
