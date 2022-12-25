package ru.practicum.shareit.booking.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingRequestMapperImplTest {


    @InjectMocks
    private BookingRequestMapperImpl mapper;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void mapFromDto() {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setBookerId(1L);
        bookingDtoRequest.setItemId(1L);

        when(userRepository.getReferenceById(any()))
                .thenReturn(new User());
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(new Item());
        assertDoesNotThrow(() -> mapper.mapFromDto(bookingDtoRequest));
    }

    @Test
    void mapToDto() {
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.mapToDto(new Booking()));
    }
}