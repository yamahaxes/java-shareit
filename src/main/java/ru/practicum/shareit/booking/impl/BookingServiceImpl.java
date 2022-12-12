package ru.practicum.shareit.booking.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ModelMapper<Booking, BookingDtoRequest> bookingRequestMapper;
    private final ModelMapper<Booking, BookingDtoResponse> bookingResponseMapper;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository repository, UserRepository userRepository,
                              ModelMapper<Booking, BookingDtoRequest> bookingRequestMapper,
                              ModelMapper<Booking, BookingDtoResponse> bookingResponseMapper,
                              ItemRepository itemRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.bookingRequestMapper = bookingRequestMapper;
        this.bookingResponseMapper = bookingResponseMapper;
        this.itemRepository = itemRepository;
    }

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
            throw new BadRequestException("No available item found.");
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

        if (!repository.existsById(bookingId)) {
            throw new BookingNotFoundException();
        }

        if (!userRepository.existsById(userId)){
            throw new UserNotFoundException();
        }

        Booking booking = repository.getReferenceById(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new BadRequestException("User not owner.");
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

}
