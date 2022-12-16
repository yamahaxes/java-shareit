package ru.practicum.shareit.booking.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.mapper.ModelMapper;

@Component
public class BookingRequestMapperImpl implements ModelMapper<Booking, BookingDtoRequest> {

    @Override
    public Booking mapFromDto(BookingDtoRequest dto) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());

        return booking;
    }

    @Override
    public BookingDtoRequest mapToDto(Booking booking) {
        throw new UnsupportedOperationException();
    }
}
