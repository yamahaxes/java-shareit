package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getByBooker_Id(Long booker_id, Sort sort);

    // Last
    List<Booking> getByBooker_IdAndEndBefore(long booker_id, LocalDateTime end, Sort sort);

    // Future
    List<Booking> getByBooker_IdAndStartAfter(long booker_id, LocalDateTime start, Sort sort);

    // Status
    List<Booking> getByBooker_IdAndStatusEquals(long booker_id, BookingStatus status, Sort sort);

    // Current
    @Query( "SELECT " +
            "   booking " +
            "FROM Booking AS booking " +
            "WHERE booking.booker.id = ?1 " +
            "AND ?2 BETWEEN booking.start AND booking.end ")
    List<Booking> getByBooker_IdAndDateBetweenStartAndEnd(long booker_id, LocalDateTime dateTime, Sort sort);


    List<Booking> getByItem_Owner_Id(long ownerId, Sort sort);

    // Last
    List<Booking> getByItem_Owner_idAndEndBefore(long ownerId, LocalDateTime end, Sort sort);

    // Future
    List<Booking> getByItem_Owner_idAndStartAfter(long ownerId, LocalDateTime start, Sort sort);

    // Status
    List<Booking> getByItem_Owner_idAndStatusEquals(long ownerId, BookingStatus status, Sort sort);

    // Current
    @Query( "SELECT " +
            "   booking " +
            "FROM Booking AS booking " +
            "WHERE booking.item.owner.id = ?1 " +
            "   AND ?2 BETWEEN booking.start AND booking.end ")
    List<Booking> getByItem_Owner_idAndBetweenStartAndEnd(long ownerId, LocalDateTime dateTime, Sort sort);

    Booking getFirstByItem_IdAndEndBeforeOrderByEndDesc(long item_id, LocalDateTime end);

    Booking getFirstByItem_IdAndStartAfterOrderByStartAsc(long item_id, LocalDateTime start);

    long countAllByBooker_IdAndItem_IdAndEndBefore(long bookerId, long itemId, LocalDateTime end);
}
