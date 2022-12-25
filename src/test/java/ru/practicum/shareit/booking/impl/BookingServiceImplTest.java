package ru.practicum.shareit.booking.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository repository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;

    @Mock
    private ModelMapper<Booking, BookingDtoRequest> bookingRequestMapper;
    @Mock
    private ModelMapper<Booking, BookingDtoResponse> bookingResponseMapper;

    @InjectMocks
    private BookingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BookingServiceImpl(repository,
                itemRepository,
                userService,
                bookingRequestMapper,
                bookingResponseMapper);
    }

    @Test
    void create() {

        BookingDtoRequest bookingDtoRequest = makeBookingDtoRequest(0);
        BookingDtoResponse bookingDtoResponse = makeBookingDtoResponse(0);
        Booking booking = new Booking();

        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setAvailable(true);
        item.setOwner(owner);


        when(itemRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(any()))
                .thenReturn(item);
        when(repository.checkBookingIntersectionByItem_Id(anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRequestMapper.mapFromDto(any()))
                .thenReturn(booking);
        when(repository.save(booking))
                .thenReturn(booking);
        when(bookingResponseMapper.mapToDto(any()))
                .thenReturn(bookingDtoResponse);

        assertEquals(bookingDtoResponse, service.create(bookingDtoRequest, 1L));

        verify(repository, times(1))
                .save(booking);
    }

    @Test
    void create_whenWrongUser_thenNotFoundException() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class,
                () -> service.create(new BookingDtoRequest(), 1));
    }

    @Test
    void create_whenWrongItem_thenNotFoundException() {
        when(itemRepository.existsById(any()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.create(new BookingDtoRequest(), 1));
    }

    @Test
    void create_whenItemNotAvailable_thenBadRequestException() {
        Item item = new Item();
        item.setAvailable(false);

        when(itemRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(any()))
                .thenReturn(item);
        assertThrows(BadRequestException.class,
                () -> service.create(new BookingDtoRequest(), anyLong()));
    }

    @Test
    void create_whenUserIsOwner_thenNotFoundException() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setAvailable(true);

        item.setOwner(owner);

        when(itemRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(any()))
                .thenReturn(item);

        assertThrows(NotFoundException.class,
                () -> service.create(new BookingDtoRequest(), 1));
    }

    @Test
    void create_whenStartAfterEnd_thenBadRequestException() {
        User owner = new User();
        owner.setId(10L);
        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(1).minusMinutes(10));

        when(itemRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(any()))
                .thenReturn(item);
        assertThrows(BadRequestException.class,
                () -> service.create(bookingDtoRequest, 1));
    }

    @Test
    void create_whenStartBeforeNow_thenBadRequestException() {
        User owner = new User();
        owner.setId(10L);
        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setStart(LocalDateTime.now().minusDays(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(1).minusMinutes(10));

        when(itemRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(any()))
                .thenReturn(item);
        assertThrows(BadRequestException.class,
                () -> service.create(bookingDtoRequest, 1));
    }

    @Test
    void create_whenBookingIntersectsWithOtherBooking_whenBadRequestException() {
        User owner = new User();
        owner.setId(10L);
        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().plusHours(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(1));

        when(itemRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(any()))
                .thenReturn(item);
        when(repository.checkBookingIntersectionByItem_Id(anyLong(),
                eq(bookingDtoRequest.getStart()),
                eq(bookingDtoRequest.getEnd())))
                .thenReturn(Optional.of(new Booking()));

        assertThrows(BadRequestException.class,
                () -> service.create(bookingDtoRequest, 1));
    }

    @Test
    void approve() {

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(repository.existsById(any()))
                .thenReturn(true);
        when(repository.getReferenceById(any()))
                .thenReturn(booking);
        when(repository.save(booking))
                .thenReturn(booking);

        service.approve(1, 1, true);

        verify(repository, times(1)).save(booking);

    }

    @Test
    void approve_whenWrongBooking_thenNotFoundException() {
        when(repository.existsById(any()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.approve(1, 1,true));
    }

    @Test
    void approve_whenWrongUser_thenNotFoundException() {
        when(repository.existsById(any()))
                .thenReturn(true);
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class,
                () -> service.approve(1, 1, true));
    }

    @Test
    void approve_whenOwnerIsNotUser_thenNotFoundException() {
        User owner = new User();
        owner.setId(2L);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setItem(item);

        when(repository.existsById(any()))
                .thenReturn(true);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(booking);

        assertThrows(NotFoundException.class,
                () -> service.approve(1, 1, true));
    }

    @Test
    void approve_whenBookingStatusIsApprovedOrRejected_thenBadRequestException() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setItem(item);

        when(repository.existsById(any()))
                .thenReturn(true);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(booking);

        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(BadRequestException.class,
                () -> service.approve(1, 1, true));

        booking.setStatus(BookingStatus.REJECTED);
        assertThrows(BadRequestException.class,
                () -> service.approve(1, 1, true));
    }

    @Test
    void approve_whenApproved_thenApprovedStatus() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setItem(item);

        when(repository.existsById(any()))
                .thenReturn(true);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(booking);
        service.approve(1, 1, true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());

    }

    @Test
    void approve_whenNotApproved_thenRejectedStatus() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setItem(item);

        when(repository.existsById(any()))
                .thenReturn(true);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(booking);
        service.approve(1, 1, false);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());

    }

    @Test
    void getById() {

        User booker = new User();
        booker.setId(1L);

        User owner = new User();
        owner.setId(9L);
        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);

        when(repository.existsById(anyLong()))
                .thenReturn(true);
        when(repository.getReferenceById(any()))
                .thenReturn(booking);
        when(bookingResponseMapper.mapToDto(any()))
                .thenReturn(new BookingDtoResponse());
        service.getById(1, 1);
        verify(bookingResponseMapper).mapToDto(any());
    }

    @Test
    void getById_whenWrongBooking_thenNotFoundException() {

        when(repository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.getById(1, 1));
    }

    @Test
    void getById_whenBookerIsNotUserAndUserIsNotOwner_thenNotFoundException() {
        User booker = new User();
        booker.setId(2L);

        User owner = new User();
        owner.setId(3L);
        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);

        when(repository.existsById(anyLong()))
                .thenReturn(true);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(booking);
        assertThrows(NotFoundException.class,
                () -> service.getById(1, 1));
    }

    @Test
    void getAllBooked() {

        assertDoesNotThrow(() -> service.getAllBooked(1, "ALL", 0, 10));

    }

    @Test
    void getAllBooked_whenWrongUser_thenNotFoundExcpetion() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());
        assertThrows(NotFoundException.class,
                () -> service.getAllBooked(1, "ALL", 0, 100));
    }

    @Test
    void getAllByOwner() {

        assertDoesNotThrow(() -> service.getAllByOwner(1, "ALL", 0, 100));

    }

    @Test
    void getAllByOwner_whenWrongUser_thenNotFoundException() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());
        assertThrows(NotFoundException.class,
                () -> service.getAllByOwner(1, "ALL", 0, 100));
    }

    private BookingDtoRequest makeBookingDtoRequest(int i) {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();

        bookingDtoRequest.setItemId(i + 1L);
        bookingDtoRequest.setStart(LocalDateTime.now().plusDays(i + 1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(i + 3));
        bookingDtoRequest.setBookerId(i + 1L);

        return bookingDtoRequest;
    }

    private BookingDtoResponse makeBookingDtoResponse(int i) {
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();
        bookingDtoResponse.setId(i + 1);
        bookingDtoResponse.setStart(LocalDateTime.now().plusDays(i + 1));
        bookingDtoResponse.setEnd(LocalDateTime.now().plusDays(i + 3));

        return bookingDtoResponse;
    }
}