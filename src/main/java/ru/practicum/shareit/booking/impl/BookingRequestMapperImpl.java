package ru.practicum.shareit.booking.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor
@Component
public class BookingRequestMapperImpl implements ModelMapper<Booking, BookingDtoRequest> {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking mapFromDto(BookingDtoRequest dto) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());

        User booker = userRepository.getReferenceById(dto.getBookerId());
        Item item = itemRepository.getReferenceById(dto.getItemId());

        booking.setBooker(booker);
        booking.setItem(item);

        return booking;
    }

    @Override
    public BookingDtoRequest mapToDto(Booking booking) {
        throw new UnsupportedOperationException();
    }
}
