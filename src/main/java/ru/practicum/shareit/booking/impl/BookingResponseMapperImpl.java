package ru.practicum.shareit.booking.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor
@Component
public class BookingResponseMapperImpl implements ModelMapper<Booking, BookingDtoResponse> {

    private final ModelMapper<Item, ItemDto> itemMapper;
    private final ModelMapper<User, UserDto> userMapper;

    @Override
    public Booking mapFromDto(BookingDtoResponse dto) {
        // not used
        return null;
    }

    @Override
    public BookingDtoResponse mapToDto(Booking booking) {
        BookingDtoResponse dto = new BookingDtoResponse();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setItem(itemMapper.mapToDto(booking.getItem()));
        dto.setBooker(userMapper.mapToDto(booking.getBooker()));

        return dto;
    }
}
