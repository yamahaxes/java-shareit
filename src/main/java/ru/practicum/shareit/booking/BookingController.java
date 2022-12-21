package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingDtoResponse create(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {

        return service.create(bookingDtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approve(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam boolean approved) {

        return service.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId) {
        return service.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBooked(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(required = false) Optional<Integer> from,
                                                 @RequestParam(required = false) Optional<Integer> size) {

        if (from.isEmpty() && size.isEmpty()) {
            return service.getAllBooked(userId, state, 0, Integer.MAX_VALUE);
        } else if (from.isEmpty() || size.isEmpty()) {
            throw new BadRequestException();
        }

        return service.getAllBooked(userId, state, from.get(), size.get());
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(required = false) Optional<Integer> from,
                                                  @RequestParam(required = false) Optional<Integer> size) {

        if (from.isEmpty() && size.isEmpty()) {
            return service.getAllByOwner(userId, state, 0, Integer.MAX_VALUE);
        } else if (from.isEmpty() || size.isEmpty()) {
            throw new BadRequestException();
        }

        return service.getAllByOwner(userId, state, from.get(), size.get());
    }
}
