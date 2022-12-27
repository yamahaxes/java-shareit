package ru.practicum.shareit.booking.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingResponseMapperImplTest {

    @InjectMocks
    private BookingResponseMapperImpl mapper;

    @Mock
    private ModelMapper<Item, ItemDto> itemMapper;
    @Mock
    private ModelMapper<User, UserDto> userMapper;

    @Test
    void mapFromDto() {
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.mapFromDto(new BookingDtoResponse()));
    }

    @Test
    void mapToDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);

        assertDoesNotThrow(() -> mapper.mapToDto(booking));
    }
}