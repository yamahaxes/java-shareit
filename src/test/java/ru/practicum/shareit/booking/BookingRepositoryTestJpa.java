package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTestJpa {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private Item item;
    private User booker;
    private User booker2;
    private User owner;
    private User owner2;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {

        booker = new User();
        booker.setName("user");
        booker.setEmail("user@mail.com");
        booker = userRepository.save(booker);

        booker2 = new User();
        booker2.setName("user2");
        booker2.setEmail("user2@mail.com");
        booker2 = userRepository.save(booker2);

        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.com");
        owner = userRepository.save(owner);

        owner2 = new User();
        owner2.setName("owner2");
        owner2.setEmail("owner2@mail.com");
        owner2 = userRepository.save(owner2);

        item = new Item();
        item.setDescription("desc");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setName("name");
        item = itemRepository.save(item);

    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void getByBooker_IdAndDateBetweenStartAndEnd() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(booker2);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setStatus(BookingStatus.WAITING);
        booking2 = bookingRepository.save(booking2);

        Pageable pageable = PageRequest.of(0, 100);
        List<Booking> bookingList = bookingRepository.getByBooker_IdAndDateBetweenStartAndEnd(
                booker2.getId(), LocalDateTime.now().plusHours(1),
                pageable
        );

        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());

        bookingList = bookingRepository.getByBooker_IdAndDateBetweenStartAndEnd(
                booker2.getId(), LocalDateTime.now().minusHours(1),
                pageable
        );

        assertEquals(0, bookingList.size());
        bookingList = bookingRepository.getByBooker_IdAndDateBetweenStartAndEnd(
                booker.getId(), LocalDateTime.now().plusHours(1),
                pageable
        );

        assertEquals(1, bookingList.size());

    }

    @Test
    void getByItem_Owner_idAndBetweenStartAndEnd() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(booker2);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setStatus(BookingStatus.WAITING);
        booking2 = bookingRepository.save(booking2);

        Pageable pageable = PageRequest.of(0, 100);

        List<Booking> bookingList = bookingRepository.getByItem_Owner_idAndBetweenStartAndEnd(
                owner.getId(),
                LocalDateTime.now(),
                pageable
        );

        assertEquals(2, bookingList.size());

        bookingList = bookingRepository.getByItem_Owner_idAndBetweenStartAndEnd(
                owner.getId(),
                LocalDateTime.now().plusMonths(1),
                pageable
        );
        assertEquals(0, bookingList.size());

        bookingList = bookingRepository.getByItem_Owner_idAndBetweenStartAndEnd(
                100,
                LocalDateTime.now(),
                pageable
        );

        assertEquals(0, bookingList.size());

    }

    @Test
    void checkBookingIntersectionByItem_Id() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(booker2);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2 = bookingRepository.save(booking2);

        Optional<Booking> bookingOptional = bookingRepository.checkBookingIntersectionByItem_Id(
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60)
        );

        assertTrue(bookingOptional.isPresent());
        assertEquals(booking2.getId(), bookingOptional.get().getId());

        bookingOptional = bookingRepository.checkBookingIntersectionByItem_Id(
                item.getId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1).plusMinutes(60)
        );

        assertTrue(bookingOptional.isEmpty());

    }

    @Test
    void getApprovedBookingsByItem_Ids() {

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(booker2);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2 = bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.getApprovedBookingsByItem_Ids(List.of(item.getId()));
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());

    }
}