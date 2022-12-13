package ru.practicum.shareit.booking.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoResponseInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.mapper.ModelMapper;

@Component
public class BookingResponseInfoMapperImpl implements ModelMapper<Booking, BookingDtoResponseInfo> {

    @Override
    public Booking mapFromDto(BookingDtoResponseInfo dto) {
        // not used
        return null;
    }

    @Override
    public BookingDtoResponseInfo mapToDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDtoResponseInfo dto = new BookingDtoResponseInfo();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());

        return dto;
    }

}
