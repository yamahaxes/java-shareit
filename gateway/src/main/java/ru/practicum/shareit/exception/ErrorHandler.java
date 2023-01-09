package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    /*@ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {NotFoundException.class})
    public Map<String, String> entityNotFound(RuntimeException e) {
        String message = e.getMessage() == null ? "" : e.getMessage();
        return Map.of("error", message);
    }*/

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {BadRequestException.class})
    public Map<String, String> badRequest(RuntimeException e) {
        String message = e.getMessage() == null ? "" : e.getMessage();
        return Map.of("error", message);
    }

}
