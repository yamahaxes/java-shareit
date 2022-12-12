package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

@Service
public interface BookingService {

    BookingDtoResponse create(BookingDtoRequest bookingDtoRequest, long userId);

}
