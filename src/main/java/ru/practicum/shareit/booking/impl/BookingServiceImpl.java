package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingList;
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
import ru.practicum.shareit.page.CustomRequestPage;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingList bookingList;

    private final ModelMapper<Booking, BookingDtoRequest> bookingRequestMapper;
    private final ModelMapper<Booking, BookingDtoResponse> bookingResponseMapper;

    @Override
    public BookingDtoResponse create(BookingDtoRequest bookingDtoRequest, long userId) {

        userService.existsUserByUserIdOrThrow(userId);

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

        Optional<Booking> foundBooking = repository.checkBookingIntersectionByItem_Id(bookingDtoRequest.getItemId(),
                bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd());

        if (foundBooking.isPresent()) {
            throw new BadRequestException("Already have a booking.");
        }

        bookingDtoRequest.setBookerId(userId);
        Booking booking = bookingRequestMapper.mapFromDto(bookingDtoRequest);
        booking.setStatus(BookingStatus.WAITING);

        return bookingResponseMapper.mapToDto(
                repository.save(booking)
        );

    }

    @Override
    public BookingDtoResponse approve(long bookingId, long userId, boolean approved) {

        existsBookingByIdOrThrow(bookingId);

        userService.existsUserByUserIdOrThrow(userId);

        Booking booking = repository.getReferenceById(bookingId);
        Item item = booking.getItem();

        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("User is not the owner.");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("Booking already approved.");
        } else if (booking.getStatus() == BookingStatus.REJECTED) {
            throw new BadRequestException("Booking already rejected.");
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
    public List<BookingDtoResponse> getAllBooked(long userId, String state, int from, int size) {
        userService.existsUserByUserIdOrThrow(userId);

        Pageable requestPage = new CustomRequestPage(from, size, Sort.by("start").descending());

        return bookingListToDtoResponse(
                bookingList.getByBookerId(state, userId, requestPage)
        );
    }

    @Override
    public List<BookingDtoResponse> getAllByOwner(long ownerId, String state, int from, int size) {
        userService.existsUserByUserIdOrThrow(ownerId);

        Pageable pageRequest = new CustomRequestPage(from, size, Sort.by("start").descending());

        return bookingListToDtoResponse(
               bookingList.getByItem_Owner_Id(state, ownerId, pageRequest)
        );
    }

    @Override
    public void existsBookingByIdOrThrow(long bookingId) {
        if (!repository.existsById(bookingId)) {
            throw new NotFoundException("Booking not found.");
        }
    }

    private List<BookingDtoResponse> bookingListToDtoResponse(List<Booking> bookings) {
        return bookings
                .stream()
                .map(bookingResponseMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
