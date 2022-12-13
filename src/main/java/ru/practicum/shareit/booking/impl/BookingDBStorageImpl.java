package ru.practicum.shareit.booking.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingRepository;

@Repository
@AllArgsConstructor
public class BookingDBStorageImpl {

    private final BookingRepository repository;

}
