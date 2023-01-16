package ru.practicum.shareit.booking.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoResponseInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingResponseInfoMapperImplTest {

    @InjectMocks
    private BookingResponseInfoMapperImpl mapper;

    @Test
    void mapFromDto() {

        assertThrows(UnsupportedOperationException.class,
                () -> mapper.mapFromDto(new BookingDtoResponseInfo()));

    }

    @Test
    void mapToDto() {

        User booker = new User();
        booker.setId(2L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);

        assertDoesNotThrow(() -> mapper.mapToDto(booking));
    }

    @Test
    void mapToDto_whenBookingIsNull_thenReturnNull() {
        assertNull(mapper.mapToDto(null));
    }
}