package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTestIT {

    @Autowired
    private BookingServiceImpl service;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private User booker1;
    private User booker2;

    @BeforeEach
    void setUp() {

        User owner = makeUser(0);
        owner = userRepository.save(owner);

        Item item = makeItem(0);
        item.setOwner(owner);
        item = itemRepository.save(item);

        booker1 = makeUser(1);
        booker2 = makeUser(2);

        userRepository.save(booker1);
        userRepository.save(booker2);

        booking1 = makeBooking(0, booker1, item);
        booking1.setStatus(BookingStatus.WAITING);
        booking2 = makeBooking(1, booker2, item);
        booking2.setStatus(BookingStatus.WAITING);
        booking3 = makeBooking(2, booker1, item);
        booking3.setStatus(BookingStatus.WAITING);

        booking1 = bookingRepository.save(booking1);
        booking2 = bookingRepository.save(booking2);
        booking3 = bookingRepository.save(booking3);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void getAllBooked() {

        List<BookingDtoResponse> bookingDtoResponses = service.getAllBooked(
                booker1.getId(),
                "WAITING",
                0, 5
        );

        assertEquals(2, bookingDtoResponses.size());
        assertEquals(booking3.getId(), bookingDtoResponses.get(0).getId());
        assertEquals(booking1.getId(), bookingDtoResponses.get(1).getId());

        bookingDtoResponses = service.getAllBooked(booker2.getId(), "WAITING", 0, 5);
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(booking2.getId(), bookingDtoResponses.get(0).getId());

        bookingDtoResponses = service.getAllBooked(booker2.getId(), "REJECTED", 0, 5);
        assertEquals(0, bookingDtoResponses.size());
    }

    @Test
    void getAllBooked_whenWrongFromAndSize_thenBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> service.getAllBooked(booker2.getId(), "WAITING", -1, 10));
        assertThrows(BadRequestException.class,
                () -> service.getAllBooked(booker2.getId(), "WAITING", 0, 0));
        assertThrows(BadRequestException.class,
                () -> service.getAllBooked(booker1.getId(), "WAITING", -1, -5));

    }

    @Test
    void getAllBooked_whenWrongState_thenBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> service.getAllBooked(booker2.getId(), "APPROVED", 0, 10));
    }

    private Booking makeBooking(int i, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(i));
        booking.setEnd(LocalDateTime.now().plusDays(i).plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);


        return booking;
    }

    private Item makeItem(int i) {
        Item item = new Item();
        item.setAvailable(true);
        item.setName("item" + i);
        item.setDescription("item" + i);
        return item;
    }

    private User makeUser(int i) {
        User user = new User();
        user.setName("name" + i);
        user.setEmail("mail" + i + "@mail.com");
        return user;
    }
}