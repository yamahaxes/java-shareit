package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    BookingService service;

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
                                     @PathVariable String bookingId) {

        return null;
    }

    @GetMapping
    public List<BookingDtoResponse> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {

        return null;
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "ALL") String state){

        return null;
    }
}
