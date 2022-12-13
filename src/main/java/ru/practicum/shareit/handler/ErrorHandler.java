package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {UserNotFoundException.class,
            ItemNotFoundException.class,
            BookingNotFoundException.class})
    public Map<String, String> entityNotFound(RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {IncorrectPeriodException.class,
            NoAvailableItemNotFoundException.class,
            UnknownStateException.class,
            BookingAlreadyApprovedException.class,
            NotFoundBookingException.class})
    public Map<String, String> badRequest(RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {UserNotOwnerOrBookerException.class,
            UserNotOwnerException.class,
            UserIsOwnerException.class})
    public Map<String, String> notFound(RuntimeException e) {
        return Map.of("error", e.getMessage());
    }
}
