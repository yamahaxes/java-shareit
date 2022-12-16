package ru.practicum.shareit.booking.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        existsUserByIdOrThrow(userId);

        if (!itemRepository.existsById(bookingDtoRequest.getItemId())) {
            throw new NotFoundException();
        }

        Item item = itemRepository.getReferenceById(bookingDtoRequest.getItemId());
        if (!item.getAvailable()) {
            throw new BadRequestException("No available item not found.");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("User is the owner the item.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!bookingDtoRequest.getStart().isBefore(bookingDtoRequest.getEnd())
                || bookingDtoRequest.getStart().isBefore(now)) {
            throw new BadRequestException("Incorrect period.");
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

        existsBookingByIdOrThrow(bookingId);

        existsUserByIdOrThrow(userId);

        Booking booking = repository.getReferenceById(bookingId);
        Item item = booking.getItem();

        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("User is not the owner.");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("Booking already approved.");
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

        existsBookingByIdOrThrow(bookingId);

        Booking booking = repository.getReferenceById(bookingId);
        if (booking.getBooker().getId() != userId
                && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("User not owner or booker.");
        }

        return bookingResponseMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllBooked(long userId, String state) {

        LocalDateTime now = LocalDateTime.now();

        existsUserByIdOrThrow(userId);

        List<Booking> bookings = new ArrayList<>();

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
                unknownStateThrow(state.toUpperCase());
        }

        return bookingListToDtoResponse(bookings);
    }

    @Override
    public List<BookingDtoResponse> getAllByOwner(long ownerId, String state) {

        LocalDateTime now = LocalDateTime.now();

        existsUserByIdOrThrow(ownerId);

        List<Booking> bookings = new ArrayList<>();

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
               unknownStateThrow(state.toUpperCase());
        }


        return bookingListToDtoResponse(bookings);
    }


    private void existsUserByIdOrThrow(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException();
        }
    }

    private void existsBookingByIdOrThrow(long bookingId) {
        if (!repository.existsById(bookingId)) {
            throw new NotFoundException();
        }
    }

    private void unknownStateThrow(String state) {
        throw new BadRequestException("Unknown state: " + state);
    }

    private List<BookingDtoResponse> bookingListToDtoResponse(List<Booking> bookings) {
        return bookings
                .stream()
                .map(bookingResponseMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
