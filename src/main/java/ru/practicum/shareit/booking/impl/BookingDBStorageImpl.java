package ru.practicum.shareit.booking.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingRepository;

@Repository
public class BookingDBStorageImpl {

    private final BookingRepository repository;

    @Autowired
    public BookingDBStorageImpl(BookingRepository repository) {
        this.repository = repository;
    }
}
