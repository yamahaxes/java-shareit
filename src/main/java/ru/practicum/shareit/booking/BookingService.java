package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Service
public interface BookingService {

    BookingDtoResponse create(BookingDtoRequest bookingDtoRequest, long userId);

    BookingDtoResponse approve(long bookingId, long userId, boolean approved);

    BookingDtoResponse getById(long bookingId, long userId);

    List<BookingDtoResponse> getAllBooked(long userId, String state);

    List<BookingDtoResponse> getAllByOwner(long ownerId, String state);
}