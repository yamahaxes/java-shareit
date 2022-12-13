package ru.practicum.shareit.booking.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ModelMapper<Booking, BookingDtoRequest> bookingRequestMapper;
    private final ModelMapper<Booking, BookingDtoResponse> bookingResponseMapper;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoResponse create(BookingDtoRequest bookingDtoRequest, long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        if (!itemRepository.existsById(bookingDtoRequest.getItemId())) {
            throw new ItemNotFoundException();
        }

        Item item = itemRepository.getReferenceById(bookingDtoRequest.getItemId());
        if (!item.getAvailable()){
            throw new NoAvailableItemNotFoundException();
        }

        if (item.getOwner().getId() == userId) {
            throw new UserIsOwnerException();
        }

        LocalDateTime now = LocalDateTime.now();
        if (!bookingDtoRequest.getStart().isBefore(bookingDtoRequest.getEnd())
                || bookingDtoRequest.getStart().isBefore(now)) {
            throw new IncorrectPeriodException();
        }

        User booker = userRepository.getReferenceById(userId);
        Booking booking = bookingRequestMapper.mapFromDto(bookingDtoRequest);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingResponseMapper.mapToDto(
                repository.save(booking)
        );

    }

    @Override
    public BookingDtoResponse approve(long bookingId, long userId, boolean approved) {

        if (!repository.existsById(bookingId)) {
            throw new BookingNotFoundException();
        }

        if (!userRepository.existsById(userId)){
            throw new UserNotFoundException();
        }

        Booking booking = repository.getReferenceById(bookingId);
        Item item = booking.getItem();

        if (item.getOwner().getId() != userId) {
            throw new UserNotOwnerException();
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingAlreadyApprovedException();
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingResponseMapper.mapToDto(
                repository.save(booking)
        );
    }

    @Override
    public BookingDtoResponse getById(long bookingId, long userId) {

        if (!repository.existsById(bookingId)) {
            throw new BookingNotFoundException();
        }

        Booking booking = repository.getReferenceById(bookingId);
        if (booking.getBooker().getId() != userId
                && booking.getItem().getOwner().getId() != userId) {
            throw new UserNotOwnerOrBookerException();
        }

        return bookingResponseMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllBooked(long userId, String state) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        List<Booking> bookings;

        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = repository.getByBooker_Id(userId, Sort.by("start").descending());
                break;
            case "CURRENT":
                bookings = repository.getByBooker_IdAndDateBetweenStartAndEnd(userId, now, Sort.by("start").descending());
                break;
            case "PAST":
                bookings = repository.getByBooker_IdAndEndBefore(userId, now, Sort.by("start").descending());
                break;
            case "FUTURE":
                bookings = repository.getByBooker_IdAndStartAfter(userId, now, Sort.by("start").descending());
                break;
            case "WAITING":
                bookings = repository.getByBooker_IdAndStatusEquals(userId, BookingStatus.WAITING, Sort.by("start").descending());
                break;
            case "REJECTED":
                bookings = repository.getByBooker_IdAndStatusEquals(userId, BookingStatus.REJECTED, Sort.by("start").descending());
                break;
            default:
                throw new UnknownStateException();
        }

        return bookingListToDtoResponse(bookings);
    }

    @Override
    public List<BookingDtoResponse> getAllByOwner(long ownerId, String state) {

        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException();
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = repository.getByItem_Owner_Id(ownerId, Sort.by("start").descending());
                break;
            case "CURRENT":
                bookings = repository.getByItem_Owner_idAndBetweenStartAndEnd(ownerId, now, Sort.by("start").descending());
                break;
            case "PAST":
                bookings = repository.getByItem_Owner_idAndEndBefore(ownerId, now, Sort.by("start").descending());
                break;
            case "FUTURE":
                bookings = repository.getByItem_Owner_idAndStartAfter(ownerId, now, Sort.by("start").descending());
                break;
            case "WAITING":
                bookings = repository.getByItem_Owner_idAndStatusEquals(ownerId, BookingStatus.WAITING, Sort.by("start").descending());
                break;
            case "REJECTED":
                bookings = repository.getByItem_Owner_idAndStatusEquals(ownerId, BookingStatus.REJECTED, Sort.by("start").descending());
                break;
            default:
               throw new UnknownStateException();
        }


        return bookingListToDtoResponse(bookings);
    }


    private List<BookingDtoResponse> bookingListToDtoResponse(List<Booking> bookings) {
        return bookings
                .stream()
                .map(bookingResponseMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
