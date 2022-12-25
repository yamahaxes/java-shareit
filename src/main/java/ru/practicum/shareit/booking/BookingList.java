package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class BookingList {
    private final BookingRepository repository;

    public List<Booking> getByBookerId(String state, long userId, Pageable requestPage) {

        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "ALL":
                return repository.getByBooker_Id(userId, requestPage);
            case "CURRENT":
                return repository.getByBooker_IdAndDateBetweenStartAndEnd(userId, now, requestPage);
            case "PAST":
                return repository.getByBooker_IdAndEndBefore(userId, now, requestPage);
            case "FUTURE":
                return repository.getByBooker_IdAndStartAfter(userId, now, requestPage);
            case "WAITING":
                return repository.getByBooker_IdAndStatusEquals(userId, BookingStatus.WAITING, requestPage);
            case "REJECTED":
                return repository.getByBooker_IdAndStatusEquals(userId, BookingStatus.REJECTED, requestPage);
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
    }

    public List<Booking> getByItem_Owner_Id(String state, long ownerId, Pageable pageRequest) {
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                return repository.getByItem_Owner_Id(ownerId, pageRequest);
            case "CURRENT":
                return repository.getByItem_Owner_idAndBetweenStartAndEnd(ownerId, now, pageRequest);
            case "PAST":
                return repository.getByItem_Owner_idAndEndBefore(ownerId, now, pageRequest);
            case "FUTURE":
                return repository.getByItem_Owner_idAndStartAfter(ownerId, now, pageRequest);
            case "WAITING":
                return repository.getByItem_Owner_idAndStatusEquals(ownerId, BookingStatus.WAITING, pageRequest);
            case "REJECTED":
                return repository.getByItem_Owner_idAndStatusEquals(ownerId, BookingStatus.REJECTED, pageRequest);
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
    }
}
