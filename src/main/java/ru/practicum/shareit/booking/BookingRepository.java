package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getByBooker_Id(Long bookerId, Pageable pageable);

    // Last
    List<Booking> getByBooker_IdAndEndBefore(long bookerId, LocalDateTime end, Pageable pageable);

    // Future
    List<Booking> getByBooker_IdAndStartAfter(long bookerId, LocalDateTime start, Pageable pageable);

    // Status
    List<Booking> getByBooker_IdAndStatusEquals(long bookerId, BookingStatus status, Pageable pageable);

    // Current
    @Query("SELECT " +
           "   booking " +
           "FROM Booking AS booking " +
           "WHERE booking.booker.id = ?1 " +
           "AND ?2 BETWEEN booking.start AND booking.end ")
    List<Booking> getByBooker_IdAndDateBetweenStartAndEnd(long bookerId, LocalDateTime dateTime, Pageable pageable);


    List<Booking> getByItem_Owner_Id(long ownerId, Pageable pageable);

    // Last
    List<Booking> getByItem_Owner_idAndEndBefore(long ownerId, LocalDateTime end, Pageable pageable);

    // Future
    List<Booking> getByItem_Owner_idAndStartAfter(long ownerId, LocalDateTime start, Pageable pageable);

    // Status
    List<Booking> getByItem_Owner_idAndStatusEquals(long ownerId, BookingStatus status, Pageable pageable);

    // Current
    @Query("SELECT " +
           "   booking " +
           "FROM Booking AS booking " +
           "WHERE booking.item.owner.id = ?1 " +
           "   AND ?2 BETWEEN booking.start AND booking.end ")
    List<Booking> getByItem_Owner_idAndBetweenStartAndEnd(long ownerId, LocalDateTime dateTime, Pageable pageable);

    long countAllByBooker_IdAndItem_IdAndEndBeforeAndStatus(long bookerId, long itemId, LocalDateTime end, BookingStatus status);

    @Query("SELECT booking " +
            "FROM Booking booking " +
            "WHERE booking.item.id = ?1 " +
            "   AND (booking.start BETWEEN ?2 AND ?3 " +
            "       OR booking.end BETWEEN ?2 AND ?3" +
            "       OR ?2 BETWEEN booking.start AND booking.end" +
            "       OR ?3 BETWEEN booking.start AND booking.end) " +
            "   AND booking.status = 'APPROVED'")
    Optional<Booking> checkBookingIntersectionByItem_Id(long itemId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT " +
            "   booking " +
            "FROM Booking booking " +
            "WHERE booking.item.id IN (?1) AND booking.status = 'APPROVED' ")
    List<Booking> getApprovedBookingsByItem_Ids(List<Long> itemIds);
}
